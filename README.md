# save-the-was

실무에서 WAS가 터졌던 경험을 되살려<br/>
WAS를 구하기 위한 개선의 여정을 떠납니다.. ✈️ 얏호~<br/>
**⛑️ SAVE-THE-WAS ⛑️**

## selectList vs. cursor

### selectList

#### 실행 방법

```bash
curl -X POST "http://localhost:8080/test/worst-delete?region=A"
```

```
SQL 실행
↓
모든 row 네트워크 전송
↓
Menu 객체 N개 생성
↓
List 저장
↓
for문 시작
```

### cursor

> Cursor는:<br/>
> ResultSet을 애플리케이션 메모리에 materialize 하지 않고,<br/>
> DB 커넥션을 유지한 채 fetchSize 단위로 스트리밍 처리하는 방식.

#### 실행 방법

```bash
curl -X POST "http://localhost:8080/test/cursor-delete?region=A&chunkSize=200" 
```

```
SQL 실행
↓
ResultSet만 생성
↓
for문 시작
   ↓
   row 1 fetch
   처리
   GC 가능
   ↓
   row 2 fetch
   처리
```

### 측정 결과

> region B, 메뉴 142,481건, `-Xmx256m`, 각 1회 실행.<br/>
> 측정은 `RunMetrics`(요청 스레드 누적 할당량 + GC 횟수/시간)로 했고, 1회 측정이라 오차가 있음.

| | worst (List + 건당 UPDATE) | cursor, 버퍼링 (설정 없음) | cursor, 서버 커서 |
|---|---|---|---|
| 소요 시간 | **60,707ms** | 2,103ms | 2,387ms |
| 누적 할당량 | **2,938MB** | 550MB | 614MB |
| GC 횟수 | 94 | 48 | **21** |
| GC 시간 | 49ms | 75ms | 45ms |
| 커서 오픈 직후 힙 | - | +14MB | **0MB** |
| UPDATE 쿼리 수 | 142,481 | 143 | 143 |

- worst → cursor: 쿼리 수(14만 → 143)와 `Menu` 객체 생성이 줄어 시간 약 25배, 할당량 약 5배 감소.
- 버퍼링 → 서버 커서: 시간/할당량은 비슷하고 GC 횟수와 오픈 직후 힙에서만 차이가 남. id만 읽는 쿼리라 효과가 작게 보이며, 행이 크고 많을수록 벌어짐.

### MySQL에서 커서가 진짜 스트리밍되려면

`useCursorFetch=true`(접속 URL)와 `fetchSize`(Mapper의 `@Options`)가 **둘 다** 필요함. 하나만 있으면 MySQL 드라이버가 결과 전체를 메모리에 버퍼링함.

| 설정 | executeQuery 직후 힙 | 읽는 중 UPDATE |
|---|---|---|
| 기본값 | +46MB (전체 버퍼링) | 가능 |
| `fetchSize`만 | +46MB (무시됨) | 가능 |
| `fetchSize` + `useCursorFetch=true` | **0MB** | 가능 |
| `fetchSize=Integer.MIN_VALUE` | 0MB | **불가** (`Streaming result set is still active`) |

```yaml
# application.yml
url: jdbc:mysql://localhost:3306/labdb?...&useCursorFetch=true
```

```java
// MenuMapper
@Options(fetchSize = 1000)
@Select("...")
Cursor<Long> streamMenuIdsByRegion(@Param("region") String region);
```
