package com.example.istar.utils;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class PageWrapper<T> {
    @JSONField(ordinal = 1)
    private final Long total;
    @JSONField(ordinal = 2)
    private final List<T> rows;

    private PageWrapper(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }


    public static <T> PageWrapper<T> wrap(Long total,List<T> rows) {
        return new PageWrapper<>(total, rows);
    }
}
