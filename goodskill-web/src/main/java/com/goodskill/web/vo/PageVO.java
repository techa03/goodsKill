package com.goodskill.web.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageVO<T> implements Serializable {
    private Integer total;
    private List<T> items;

    public PageVO(IPage<T> page) {
        this.total = Math.toIntExact(page.getTotal());
        this.items = page.getRecords();
    }
}
