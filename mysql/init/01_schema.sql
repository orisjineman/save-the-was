-- store
CREATE TABLE store
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    status     ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    region     VARCHAR(50)  NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY        idx_store_region_status (region, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- menu
CREATE TABLE menu
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    store_id   BIGINT UNSIGNED NOT NULL,
    name       VARCHAR(120) NOT NULL,
    deleted    TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY        idx_menu_store_id (store_id),
    KEY        idx_menu_store_id_deleted (store_id, deleted),
    CONSTRAINT fk_menu_store
        FOREIGN KEY (store_id) REFERENCES store (id)
            ON DELETE RESTRICT
            ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- job
CREATE TABLE menu_delete_job
(
    id                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    status                ENUM('PENDING','RUNNING','DONE','FAILED') NOT NULL DEFAULT 'PENDING',
    condition_json        JSON NOT NULL,
    processed_store_count BIGINT UNSIGNED NOT NULL DEFAULT 0,
    processed_menu_count  BIGINT UNSIGNED NOT NULL DEFAULT 0,
    started_at            DATETIME(6) NULL,
    finished_at           DATETIME(6) NULL,
    last_error            TEXT NULL,
    created_at            DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at            DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY                   idx_job_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;