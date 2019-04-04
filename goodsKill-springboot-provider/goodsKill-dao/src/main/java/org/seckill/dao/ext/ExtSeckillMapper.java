package org.seckill.dao.ext;

import org.apache.ibatis.annotations.Param;
import org.seckill.dao.SeckillMapper;
import org.seckill.entity.SuccessKilled;

import java.util.Date;

/**
 * Created by heng on 2017/6/24.
 */
public interface ExtSeckillMapper extends SeckillMapper {

    /**
     * 普通update方法
     *
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 乐观锁update方法
     *
     * @param seckillId
     * @param killTime
     * @param number    商品剩余数量
     * @return
     */
    int reduceNumberOptimized(@Param("seckillId") long seckillId, @Param("killTime") Date killTime, @Param("number") int number);

    /**
     * 调用存储过程
     *
     * @return
     */
    void reduceNumberByProcedure(SuccessKilled successKilled);
}
