package com.heatguideIOT.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HeatGuideItemDTO {
    private String itemCheck;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;

    public HeatGuideItemDTO(String itemCheck, LocalDateTime startTime, LocalDateTime finishTime) {
        this.itemCheck = itemCheck;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    // Getter & Setter
}
