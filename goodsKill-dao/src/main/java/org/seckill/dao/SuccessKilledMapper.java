package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

public interface SuccessKilledMapper {
    int deleteByPrimaryKey(@Param("seckillId") Long seckillId, @Param("userPhone") String userPhone);

    int insert(SuccessKilled record);

    int insertSelective(SuccessKilled record);

    SuccessKilled selectByPrimaryKey(@Param("seckillId") Long seckillId, @Param("userPhone") String userPhone);

    int updateByPrimaryKeySelective(SuccessKilled record);

    int updateByPrimaryKey(SuccessKilled record);
}