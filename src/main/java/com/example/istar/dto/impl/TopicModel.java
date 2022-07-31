package com.example.istar.dto.impl;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
public class TopicModel {
    @ApiModelProperty(value = "文章标题")
    private String title;
    @ApiModelProperty(value = "文章内容")
    private String content;
    @ApiModelProperty(value = "文章图片id列表")
    private Set<String> pictureIds;
    @ApiModelProperty(value = "文章视频id列表")
    private Set<String> videoIds;

    @ApiModelProperty(hidden = true)
    public boolean isCorrect() {
        return ObjectUtil.isAllNotEmpty(this.getTitle(), this.getContent());
    }
}
