package com.example.istar.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class TopicAddModel extends TopicModel {
    @ApiModelProperty(hidden = true)
    @JSONField(serialize = false, deserialize = false)
    private Set<String> pictureIds;
    @ApiModelProperty(hidden = true)
    @JSONField(serialize = false, deserialize = false)
    private Set<String> videoIds;

}
