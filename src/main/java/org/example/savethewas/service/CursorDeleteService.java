package org.example.savethewas.service;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.cursor.Cursor;
import org.example.savethewas.mapper.MenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CursorDeleteService {
    private final MenuMapper menuMapper;

    public record CursorDeleteResult(long scannedIds, long updatedRows, int chunkSize, long tookMs) {
    }

    // 주의: Cursor는 ResultSet을 물고 있어서 트랜잭션/커넥션 유지 필요함!!
    @Transactional
    public CursorDeleteResult deleteMenusWithCursor(String region, int chunkSize) throws IOException {
        long t0 = System.currentTimeMillis();

        long scanned = 0;
        long updated = 0;

        List<Long> buffer = new ArrayList<>(chunkSize);

        // streamMenuIdsByRegion 호출시 : DB 커넥션 획득, SQL 실행됨, ResultSet 생성됨 / 그치만 데이터는 아직 거의 안넘어옴
        // 이 순간 DB 내부 상태!!
        // query 실행 완료, 결과 테이블(ResultSet) 준비됨, 커넥션에 결과 핸들 붙여둠
        try (Cursor<Long> cursor = menuMapper.streamMenuIdsByRegion(region)) {
            // for문 진입하면, 내부적으로 ResultSet.next() 호출
            // WAS ----(fetch request)---> DB
            // WAS <---(rows chunk)------- DB
            // 정리하면, DB Result Set에 남아있는 rows 중 일부를
            // JDBC fetch size 단위로, 네트워크 통해 WAS로 복사
            for (Long id : cursor) {
                scanned++;
                buffer.add(id);

                if (buffer.size() >= chunkSize) {
                    updated += menuMapper.softDeleteByIds(buffer); // 호출
                    buffer.clear();
                }
            }
        } // try 블록이 끝나고 나면, ResultSet close, Statement close, Connection을 pool에 반환.

        // 나머지 처리
        if (!buffer.isEmpty()) {
            updated += menuMapper.softDeleteByIds(buffer);  // 호출
            buffer.clear();
        }

        long tookMs = System.currentTimeMillis() - t0;
        return new CursorDeleteResult(scanned, updated, chunkSize, tookMs);
    }
}