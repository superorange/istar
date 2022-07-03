package com.example.istar.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CaptchaTrackerDto {
    private Integer bgImageWidth;
    private Integer bgImageHeight;
    private Integer sliderImageWidth;
    private Integer sliderImageHeight;
    private Date startSlidingTime;
    private Date endSlidingTime;
    private List<Track> trackList;

    @Data
    public static
    class Track {
        private Integer x;
        private Integer y;
        private Integer time;

    }
}
