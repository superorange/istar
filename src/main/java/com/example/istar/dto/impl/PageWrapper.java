package com.example.istar.dto.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tian
 */
@Data
@NoArgsConstructor
public class PageWrapper<T> {
    private Integer page;
    private Integer pageSize;
    private Integer total;
    List<T> rows;

    public static <T> PageWrapper<T> wrap(List<T> rows) {
        PageWrapper<T> objectPageWrapperModel = new PageWrapper<>();
        objectPageWrapperModel.setRows(new ArrayList<>());
        objectPageWrapperModel.getRows().addAll(rows);
        return objectPageWrapperModel;
    }

    public static <T> PageWrapper<T> wrap(Page<T> page) {
        PageWrapper<T> objectPageWrapperModel = new PageWrapper<>();
        objectPageWrapperModel.setRows(new ArrayList<>());
        objectPageWrapperModel.getRows().addAll(page.getRecords());
        return objectPageWrapperModel;
    }


}
