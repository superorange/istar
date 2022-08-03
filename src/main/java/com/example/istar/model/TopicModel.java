package com.example.istar.model;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.example.istar.model.inter.RequestCheckerInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * @author tian
 */
@Data
public class TopicModel implements RequestCheckerInterface {
    @ApiModelProperty(value = "文章标题")
    private String title;
    @ApiModelProperty(value = "文章内容")
    private String content;
    @ApiModelProperty(value = "图片")
    private MultipartFile[] pictures;
    @ApiModelProperty(value = "文章图片id列表")
    private Set<String> pictureIds;
    @ApiModelProperty(value = "文章视频id列表")
    private Set<String> videoIds;

    @Override
    @ApiModelProperty(hidden = true)
    public boolean isCorrect() {
        return ObjectUtil.isAllNotEmpty(this.getTitle(), this.getContent());
    }
}
