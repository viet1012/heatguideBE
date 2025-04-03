package com.heatguideIOT.demo.dto;

import lombok.Data;

import java.util.List;
@Data
public class FerthDTO {
    private String name;
    private List<LotDTO> lots;

    public FerthDTO(String name, List<LotDTO> lots) {
        this.name = name;
        this.lots = lots;
    }

    public List<LotDTO> getLots() {
        return lots;
    }

    // Getter & Setter
}
