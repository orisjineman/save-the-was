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