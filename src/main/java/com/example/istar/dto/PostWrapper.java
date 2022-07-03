package com.example.istar.dto;

import com.example.istar.entity.Pictures;
import com.example.istar.entity.Post;
import com.example.istar.entity.Videos;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PostWrapper extends Post {
    private List<Videos> videos;
    private List<Pictures> pictures;
}
