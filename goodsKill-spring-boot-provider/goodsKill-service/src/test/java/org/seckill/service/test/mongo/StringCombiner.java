package org.seckill.service.test.mongo;

import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;

/**
 * @author techa03
 * @date 2019/3/21
 */
public class StringCombiner {
    private String prefix;
    private String delim;
    private String suffix;
    private StringBuilder builder;

    public StringCombiner(String prefix, String delim, String suffix) {
        this.prefix = prefix;
        this.delim = delim;
        this.suffix = suffix;
        builder = new StringBuilder();
    }

    public StringCombiner add(String element) {
        if (areAtStart()) {
            builder.append(prefix);
        } else {
            builder.append(delim);
        }
        builder.append(element);
        return this;
    }

    public StringCombiner merge(StringCombiner stringCombiner) {
        builder.append(stringCombiner.builder);
        return this;
    }

    @Override
    public String toString() {
        return builder.toString()+suffix;
    }

    //    private boolean areAtEnd() {
//        builder.
//    }

    private boolean areAtStart() {
        return StringUtils.isEmpty(builder.toString());
    }

    public static void main(String[] args) {
//        String result = Stream.of("1", "2", "3").reduce(new StringCombiner("[", ",", "]"), StringCombiner::add, StringCombiner::merge).toString();
//        System.out.println(result);
        StringCollector collector = new StringCollector("[", ",", "]");
        String result = Lists.newArrayList("1","2").stream().collect(collector);
        System.out.println(result);

    }
}
