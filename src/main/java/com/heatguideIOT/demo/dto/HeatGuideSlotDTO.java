package com.heatguideIOT.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class HeatGuideSlotDTO {
    private String lot;
    private String ferth;
    private String itemCheck;
    private String machine;  // ✅ Thêm machine
    private LocalDateTime startTime;
    private LocalDateTime finishTime;

    private Integer Qty;
    private String POREQNO;

    // Constructors
    public HeatGuideSlotDTO(String lot, String ferth, String itemCheck, String machine, LocalDateTime startTime, LocalDateTime finishTime, String POREQNO, Integer qty) {
        this.lot = lot;
        this.ferth = ferth;
        this.itemCheck = itemCheck;
        this.machine = machine;  // ✅ Gán machine
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.POREQNO = POREQNO;
        this.Qty = qty;

    }

    // Getters and Setters
    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getFerth() {
        return ferth;
    }

    public void setFerth(String ferth) {
        this.ferth = ferth;
    }

    public String getItemCheck() {
        return itemCheck;
    }

    public void setItemCheck(String itemCheck) {
        this.itemCheck = itemCheck;
    }

    public String getMachine() {  // ✅ Getter cho machine
        return machine;
    }

    public void setMachine(String machine) {  // ✅ Setter cho machine
        this.machine = machine;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getQty() {
        return Qty;
    }

    public void setQty(Integer qty) {
        Qty = qty;
    }

    public String getPOREQNO() {
        return POREQNO;
    }

    public void setPOREQNO(String POREQNO) {
        this.POREQNO = POREQNO;
    }
}
