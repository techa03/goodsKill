package com.goodskill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodskill.entity.SuccessKilled;
import com.goodskill.mp.dao.mapper.SuccessKilledMapper;
import com.goodskill.service.common.SuccessKilledService;
import org.apache.dubbo.config.annotation.DubboService;

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
