package org.seckill.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<Example , Entity> {
    long countByExample(Example example);

    int deleteByExample(Example example);

    int deleteByPrimaryKey(Integer id);

    int insert(Entity record);

    int insertSelective(Entity record);

    List<Entity> selectByExample(Example example);

    Entity selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Entity record, @Param("example") Example example);

    int updateByExample(@Param("record") Entity record, @Param("example") Example example);

    int updateByPrimaryKeySelective(Entity record);

    int updateByPrimaryKey(Entity record);
}
