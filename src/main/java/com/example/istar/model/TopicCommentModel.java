package com.example.istar.model;

import cn.hutool.core.util.StrUtil;
import com.example.istar.model.inter.RequestCheckerInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tian
 */
@Data
public class TopicCommentModel implements RequestCheckerInterface {

    @ApiModelProperty("评论主题ID")
    private String topicId;


    @ApiModelProperty("评论内容")
    private String content;
    @ApiModelProperty(hidden = true)
    @Override
    public boolean isCorrect() {
        return StrUtil.isAllNotBlank(this.getContent(), this.getTopicId());
    }


}


