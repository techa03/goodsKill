package org.seckill.mp.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;

import java.util.Date;

/**
 * <p>
 * 秒杀库存表 Mapper 接口
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
public interface SeckillMapper extends BaseMapper<Seckill> {
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
    @Deprecated
    void reduceNumberByProcedure(SuccessKilled successKilled);
}
