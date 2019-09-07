package com.goodskill;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import org.seckill.entity.Goods;

/**
 * @author techa03
 * @date 2019/8/11
 */
public class GoodsFunnel implements Funnel<Goods> {
    @Override
    public void funnel(Goods from, PrimitiveSink into) {
        into.putInt(from.getGoodsId()).putUnencodedChars(from.getName());
    }
}
