package org.seckill.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;
import org.seckill.entity.SuccessKilledExample;
import org.seckill.entity.SuccessKilledKey;

public interface SuccessKilledMapper {
    long countByExample(SuccessKilledExample example);

    int deleteByExample(SuccessKilledExample example);

    int deleteByPrimaryKey(SuccessKilledKey key);

    int insert(SuccessKilled record);

    int insertSelective(SuccessKilled record);

    List<SuccessKilled> selectByExample(SuccessKilledExample example);

    SuccessKilled selectByPrimaryKey(SuccessKilledKey key);

    int updateByExampleSelective(@Param("record") SuccessKilled record, @Param("example") SuccessKilledExample example);

    int updateByExample(@Param("record") SuccessKilled record, @Param("example") SuccessKilledExample example);

    int updateByPrimaryKeySelective(SuccessKilled record);

    int updateByPrimaryKey(SuccessKilled record);
}