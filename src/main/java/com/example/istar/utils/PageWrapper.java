package com.example.istar.utils;

import com.github.pagehelper.PageInfo;

import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class PageWrapper<T> {
    private final Long total;
    private final List<T> rows;

    private PageWrapper(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }


    public static <T> PageWrapper<T> wrap(PageInfo<T> pageInfo) {
        return new PageWrapper<>(pageInfo.getTotal(), pageInfo.getList());
    }
}
