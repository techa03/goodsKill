package org.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.seckill.entity.SuccessKilled;
import org.seckill.mp.dao.mapper.SuccessKilledMapper;
import org.seckill.service.mp.SuccessKilledService;

/**
 * <p>
 * 秒杀成功明细表 服务实现类
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@DubboService
public class SuccessKilledServiceImpl extends ServiceImpl<SuccessKilledMapper, SuccessKilled> implements SuccessKilledService {

}
