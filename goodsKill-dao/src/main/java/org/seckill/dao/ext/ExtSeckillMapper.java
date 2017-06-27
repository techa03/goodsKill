package org.seckill.dao.ext;

import org.apache.ibatis.annotations.Param;
import org.seckill.dao.SeckillMapper;

import java.util.Date;

/**
 * Created by heng on 2017/6/24.
 */
public interface ExtSeckillMapper extends SeckillMapper{

    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);
}
