package com.heatguideIOT.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class LotInfoDTO {

    private String itemCheck;
    private String note;
    private String ferth;
    private Integer qty;
    private String poreqno;




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LotInfoDTO)) return false;
        LotInfoDTO that = (LotInfoDTO) o;
        return
                Objects.equals(poreqno, that.poreqno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ferth, qty, poreqno);
    }
}
