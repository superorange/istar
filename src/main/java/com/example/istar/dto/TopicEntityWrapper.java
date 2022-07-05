package com.example.istar.dto;

import com.example.istar.entity.PictureEntity;
import com.example.istar.entity.TopicEntity;
import com.example.istar.entity.VideoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TopicEntityWrapper extends TopicEntity {
    private List<VideoEntity> videos;
    private List<PictureEntity> pictures;
}
