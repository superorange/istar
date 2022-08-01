package com.example.istar.dto.impl;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author tian
 */
@Data
@AllArgsConstructor
public class TopicSimpleDto {
    @ApiModelProperty(value = "帖子ID")
    private String topicId;

}
