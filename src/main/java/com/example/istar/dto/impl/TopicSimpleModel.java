package com.example.istar.dto.impl;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopicSimpleModel {
    @ApiModelProperty(value = "帖子ID")
    private String topicId;

}
