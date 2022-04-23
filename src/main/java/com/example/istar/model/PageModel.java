package com.example.istar.model;

import lombok.Data;

/**
 * @author tian
 */
public class PageModel {
    private int pageIndex = 1;
    private int pageSize = 30;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isLarge() {
        return pageSize > 100;
    }

    public int getLimitIndex() {
        if (pageIndex < 1) {
            return 0;
        }
        return (pageIndex - 1) * pageSize;
    }
}
