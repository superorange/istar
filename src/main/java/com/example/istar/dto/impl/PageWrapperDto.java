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
public class PageWrapperDto<T> {
    private Integer page;
    private Integer pageSize;
    private Integer total;
    List<T> rows;

    public static <T> PageWrapperDto<T> wrap(List<T> rows) {
        PageWrapperDto<T> objectPageWrapperModel = new PageWrapperDto<>();
        objectPageWrapperModel.setRows(new ArrayList<>());
        objectPageWrapperModel.getRows().addAll(rows);
        return objectPageWrapperModel;
    }

    public static <T> PageWrapperDto<T> wrap(Page<T> page) {
        PageWrapperDto<T> objectPageWrapperModel = new PageWrapperDto<>();
        objectPageWrapperModel.setRows(new ArrayList<>());
        objectPageWrapperModel.getRows().addAll(page.getRecords());
        return objectPageWrapperModel;
    }


}
