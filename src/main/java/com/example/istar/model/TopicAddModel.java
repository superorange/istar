package com.example.istar.model;

import cn.hutool.core.util.ObjectUtil;
import com.example.istar.model.inter.RequestCheckerInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * @author tian
 */
@Data
public class TopicAddModel implements RequestCheckerInterface {
    @ApiModelProperty(value = "文章标题")
    private String title;
    @ApiModelProperty(value = "文章内容")
    private String content;
    @ApiModelProperty(value = "图片")
    private MultipartFile[] pictures;

    @Override
    @ApiModelProperty(hidden = true)
    public boolean isCorrect() {
        return ObjectUtil.isAllNotEmpty(this.getTitle(), this.getContent());
    }
}
