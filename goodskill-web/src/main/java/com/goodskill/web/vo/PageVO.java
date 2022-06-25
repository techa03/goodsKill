package com.goodskill.web.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageVO<T> implements Serializable {
    private Integer total;
    private List items;
}
