package com.example.istar.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author tian
 */
@NoArgsConstructor()
@AllArgsConstructor()
@Setter
public class PageModel {
    private Integer pageIndex;
    private Integer pageSize;


    public int getPageIndex() {
        int safe = 1;
        if (pageIndex != null && pageIndex >= 1) {
            safe = this.pageIndex;
        }
        return safe;
    }

    public int getPageSize() {
        int safe = 10;
        if (pageSize != null && pageSize >= 0) {
            safe = this.pageSize;
        }
        return safe;
    }


}
