package com.example.istar.model;

import cn.hutool.core.util.StrUtil;
import com.example.istar.model.inter.RequestCheckerInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author tian
 */
@Data
public class TopicCommentReplayModel implements RequestCheckerInterface {
    @ApiModelProperty("被评论的ID")
    private String commentId;

    @ApiModelProperty("被回复的ID")
    private String toReplyId;

    @ApiModelProperty("回复内容")
    private String content;

    @ApiModelProperty(hidden = true)
    @Override
    public boolean isCorrect() {
        return StrUtil.isAllNotBlank(content, commentId);
    }
}
