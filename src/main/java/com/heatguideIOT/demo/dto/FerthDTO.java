package com.heatguideIOT.demo.dto;

import lombok.Data;

import java.util.List;
@Data
public class FerthDTO {
    private List<LotDTO> lots;

    public FerthDTO(List<LotDTO> lots) {
        this.lots = lots;
    }

    public List<LotDTO> getLots() {
        return lots;
    }

    // Getter & Setter
}
