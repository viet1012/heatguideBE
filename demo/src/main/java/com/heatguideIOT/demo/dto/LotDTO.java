package com.heatguideIOT.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class LotDTO {
    private String lot;
    private List<HeatGuideItemDTO> items;

    public LotDTO(String lot, List<HeatGuideItemDTO> items) {
        this.lot = lot;
        this.items = items;
    }

    // Getter & Setter
}
