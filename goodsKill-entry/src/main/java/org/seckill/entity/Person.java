package org.seckill.entity;

import java.io.Serializable;

/**
 * Created by heng on 2016/8/3.
 */
public class Person implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
