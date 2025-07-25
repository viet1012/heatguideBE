package com.heatguideIOT.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HeatGuideItemDTO {
    private String itemCheck;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private String machine;
    private Integer Qty;
    private String POREQNO;
    private String ferth;

    public HeatGuideItemDTO(String itemCheck, LocalDateTime startTime, LocalDateTime finishTime,String machine,Integer qty, String POREQNO,  String ferth) {
        this.itemCheck = itemCheck;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.machine = machine;
        this.Qty = qty;
        this.POREQNO = POREQNO;
        this.ferth = ferth;
    }


    public String getPOREQNO() {
        return POREQNO;
    }

    public void setPOREQNO(String POREQNO) {
        this.POREQNO = POREQNO;
    }

    public Integer getQty() {
        return Qty;
    }

    public void setQty(Integer qty) {
        Qty = qty;
    }

    public String getFerth() {
        return ferth;
    }

    public void setFerth(String ferth) {
        this.ferth = ferth;
    }
}
