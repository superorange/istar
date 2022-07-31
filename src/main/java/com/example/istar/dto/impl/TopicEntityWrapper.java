package com.example.istar.dto.impl;

import com.example.istar.entity.PictureEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.entity.VideoEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TopicEntityWrapper extends TopicEntity {
    @ApiModelProperty(value = "视频详细信息")
    private List<VideoEntity> videos;
    @ApiModelProperty(value = "图片详细信息")
    private List<PictureEntity> pictures;
}
