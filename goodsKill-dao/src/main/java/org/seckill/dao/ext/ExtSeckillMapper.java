package org.seckill.dao.ext;

import org.apache.ibatis.annotations.Param;
import org.seckill.dao.SeckillMapper;

import java.util.Date;

/**
 * Created by heng on 2017/6/24.
 */
public interface ExtSeckillMapper extends SeckillMapper {

    /**
     * 普通update方法
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 调用存储过程
     * @param seckillId
     * @param killTime
     * @param phone
     * @return
     */
    void reduceNumberByProcedure(@Param("seckillId") long seckillId, @Param("phone") long phone,
                                @Param("killTime") Date killTime);
}
