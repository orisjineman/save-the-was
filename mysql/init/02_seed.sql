SET SESSION cte_max_recursion_depth = 3000;

INSERT INTO store (name, status, region, created_at)
WITH RECURSIVE seq AS (SELECT 1 AS n
                       UNION ALL
                       SELECT n + 1
                       FROM seq
                       WHERE n < 2000)
SELECT CONCAT('store-', LPAD(n, 4, '0'))      AS name,
       IF(RAND() < 0.9, 'ACTIVE', 'INACTIVE') AS status,
       CASE
           WHEN RAND() < 0.20 THEN 'A'
           WHEN RAND() < 0.45 THEN 'B'
           WHEN RAND() < 0.70 THEN 'C'
           ELSE 'D'
           END                                AS region,
       NOW(6)                                 AS created_at
FROM seq;

CREATE TABLE seq_300
(
    n INT NOT NULL PRIMARY KEY
) ENGINE = InnoDB;

INSERT INTO seq_300 (n)
WITH RECURSIVE seq AS (SELECT 1 AS n
                       UNION ALL
                       SELECT n + 1
                       FROM seq
                       WHERE n < 300)
SELECT n
FROM seq;

INSERT INTO menu (store_id, name, deleted, created_at)
SELECT s.id                                          AS store_id,
       CONCAT('menu-', s.id, '-', LPAD(x.n, 3, '0')) AS name,
       0                                             AS deleted,
       NOW(6)                                        AS created_at
FROM store s
         JOIN (
    -- store별 랜덤 menu_count를 미리 뽑아두기
    SELECT id,
           (50 + FLOOR(RAND(id) * 251)) AS menu_count -- 50~300
    FROM store) c ON c.id = s.id
         JOIN seq_300 x ON x.n <= c.menu_count;

drop table seq_300;