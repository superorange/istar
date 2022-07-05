package com.example.istar.model;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

import java.util.List;

@Data
public class PostModel {
    private String title;
    private String content;
    private List<String> pictureIds;
    private List<String> videoIds;

    public boolean isCorrect() {
        return ObjectUtil.isAllNotEmpty(this.getTitle(), this.getContent());
    }
}
