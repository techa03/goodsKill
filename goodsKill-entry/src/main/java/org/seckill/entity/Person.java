package org.seckill.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by heng on 2016/8/3.
 */
@Data
public class Person implements Serializable{
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;
}
