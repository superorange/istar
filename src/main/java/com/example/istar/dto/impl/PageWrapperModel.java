package com.example.istar.dto.impl;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageWrapperModel<T> {
    private Integer page;
    private Integer pageSize;
    private Integer total;
    List<T> rows;

}
