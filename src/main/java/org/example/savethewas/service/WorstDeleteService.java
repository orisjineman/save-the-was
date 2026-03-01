package org.example.savethewas.service;

import lombok.RequiredArgsConstructor;
import org.example.savethewas.domain.Menu;
import org.example.savethewas.domain.Store;
import org.example.savethewas.mapper.MenuMapper;
import org.example.savethewas.mapper.StoreMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorstDeleteService {
    // N+1 + 객체 폭발 + 업데이트 폭발
    private final StoreMapper storeMapper;
    private final MenuMapper menuMapper;

    public record WorstDeleteResult(
            long storeCount,
            long menusLoaded,
            long menusUpdated,
            long tookMs) {
    }

    // 일부러 트랜잭션 걸어두면 락/커넥션 오래 잡고 더 잘 터짐
    @Transactional
    public WorstDeleteResult deleteMenusWorst(String region) {
        long t0 = System.currentTimeMillis();

        List<Store> stores = storeMapper.findByRegion(region);

        long totalMenusLoaded = 0;
        long totalMenusUpdated = 0;

        // 약 400번 반복
        for (Store store : stores) {
            // 여기서 store당 메뉴를 "전부 List"로 메모리에 올림
            List<Menu> menus = menuMapper.findActiveByStoreId(store.getId());
            totalMenusLoaded += menus.size();

            // 그리고 메뉴 하나당 update 1번씩 날림 => 쿼리 폭발 !!!
            for (Menu menu : menus) {
                totalMenusUpdated += menuMapper.softDeleteById(menu.getId()); // 1.63GB를 잡아먹는다
            }
        }

        long tookMs = System.currentTimeMillis() - t0;
        return new WorstDeleteResult(stores.size(), totalMenusLoaded, totalMenusUpdated, tookMs);
    }
}