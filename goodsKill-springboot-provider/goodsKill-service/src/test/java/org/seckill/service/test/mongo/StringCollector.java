package org.seckill.service.test.mongo;

import org.seckill.entity.Seckill;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author techa03
 * @date 2019/3/20
 */
public class StringCollector implements Collector<String, StringCombiner, String> {
    private String prefix;
    private String delim;
    private String suffix;

    @Override
    public Supplier<StringCombiner> supplier() {
        return () -> new StringCombiner(prefix, delim, suffix);
    }

    @Override
    public BiConsumer<StringCombiner, String> accumulator() {
        return StringCombiner::add;
    }

    @Override
    public BinaryOperator<StringCombiner> combiner() {
        return StringCombiner::merge;
    }

    @Override
    public Function<StringCombiner, String> finisher() {
        return StringCombiner::toString;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>();
    }

    public StringCollector(String prefix, String delim, String suffix) {
        this.prefix = prefix;
        this.delim = delim;
        this.suffix = suffix;
    }

    public static void main(String[] args) {
        List<Seckill> list = new ArrayList();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            Seckill seckill = Seckill.builder().build();
            long e = random.nextInt(1000000) + 1000;
            seckill.setSeckillId(e);
            list.add(seckill);
        }
        list.stream().mapToLong(Seckill::getSeckillId).forEach(n -> System.out.println(n));
    }
}
