package com.heatguideIOT.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class LotDTO {
    private String lot;
    private String rfID_key;
    private List<LotInfoDTO> info;
    private List<HeatGuideItemDTO> items;

    public LotDTO(String lot, String rfID_key, List<LotInfoDTO> info, List<HeatGuideItemDTO> items) {
        this.lot = lot;
        this.rfID_key = rfID_key;
        this.info = info;
        this.items = items;
    }
}
