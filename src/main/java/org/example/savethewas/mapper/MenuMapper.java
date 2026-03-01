package org.example.savethewas.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.savethewas.domain.Menu;

import java.util.List;

@Mapper
public interface MenuMapper {
    @Select("""
                SELECT id, store_id, name, deleted, created_at
                FROM menu
                WHERE store_id = #{storeId} AND deleted = 0
            """)
    List<Menu> findActiveByStoreId(@Param("storeId") long storeId);

    @Update("""
                UPDATE menu
                SET deleted = 1
                WHERE id = #{id}
            """)
    int softDeleteById(@Param("id") long id);
}