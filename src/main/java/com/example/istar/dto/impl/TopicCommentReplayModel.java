package com.example.istar.dto.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.istar.dto.RequestCheckerInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TopicCommentReplayModel implements RequestCheckerInterface {

    @ApiModelProperty("评论用户ID")
    private String uuid;

    @ApiModelProperty("被回复评论的ID")
    private String commentId;

    @ApiModelProperty("回复评论ID")
    private String replyId;

    @ApiModelProperty("被回复的回复评论ID")
    private String toReplyId;

    @ApiModelProperty("回复内容")
    private String content;

    @ApiModelProperty(hidden = true)
    @Override
    public boolean isCorrect() {
        return StrUtil.isAllNotBlank(content, commentId);
    }
}
