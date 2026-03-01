package org.example.savethewas.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.savethewas.domain.Store;

import java.util.List;

@Mapper
public interface StoreMapper {
    @Select("""
        SELECT id, name, status, region, created_at
        FROM store
        WHERE region = #{region}
    """)
    List<Store> findByRegion(@Param("region") String region);
}