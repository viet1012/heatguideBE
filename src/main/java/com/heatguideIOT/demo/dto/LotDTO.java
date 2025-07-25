package com.heatguideIOT.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class LotDTO {
    private String lot;

    private List<LotInfoDTO> info;
    private List<HeatGuideItemDTO> items;

    public LotDTO(String lot, List<LotInfoDTO> info, List<HeatGuideItemDTO> items) {
        this.lot = lot;
        this.info = info;
        this.items = items;
    }

    // Getter & Setter
}
