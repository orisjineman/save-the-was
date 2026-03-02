package org.example.savethewas.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.cursor.Cursor;
import org.example.savethewas.domain.Menu;

import java.util.List;

@Mapper
public interface MenuMapper {
    @Select("""
                SELECT id, store_id, name, deleted, created_at
                FROM menu
                WHERE store_id = #{storeId}
                  AND deleted = 0
            """)
    List<Menu> findActiveByStoreId(@Param("storeId") long storeId);

    @Update("""
                UPDATE menu
                SET deleted = 1
                WHERE id = #{id}
            """)
    int softDeleteById(@Param("id") long id);

    // 커서로 읽을 때는 "id만" 뽑는게 제일 가볍다
    @Select("""
                SELECT m.id
                FROM menu m
                    JOIN store s
                        ON s.id = m.store_id
                WHERE s.region = #{region}
                  AND m.deleted = 0
                ORDER BY m.id
            """)
    Cursor<Long> streamMenuIdsByRegion(@Param("region") String region);

    // N개씩 배치 soft delete
    @Update({
            "<script>",
            "UPDATE menu",
            "SET deleted = 1",
            "WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int softDeleteByIds(@Param("ids") List<Long> ids);
}