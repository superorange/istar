package com.example.istar.dto;

/**
 * @author tian
 */

public class VideoQueryModel extends PageModel {
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    private String keyword;
}
