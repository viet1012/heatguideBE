package com.heatguideIOT.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class HeatGuideOutputDTO {
    private LocalDateTime starttime;
    private Integer qty;
    private String itemcheck;

    public HeatGuideOutputDTO(LocalDateTime localDateTime, Integer integer, String s) {
        this.starttime = localDateTime;
        this.qty = integer;
        this.itemcheck = s;
    }

}
