package com.heatguideIOT.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HeatGuideItemDTO {
    private String itemCheck;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private String machine;

    public HeatGuideItemDTO(String itemCheck, LocalDateTime startTime, LocalDateTime finishTime,String machine) {
        this.itemCheck = itemCheck;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.machine = machine;
    }

    // Getter & Setter
}
