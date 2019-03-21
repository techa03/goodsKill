package org.seckill.service.test.mongo;

import java.util.HashSet;
import java.util.Set;
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
    public Function<StringCombiner,String> finisher() {
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
}
