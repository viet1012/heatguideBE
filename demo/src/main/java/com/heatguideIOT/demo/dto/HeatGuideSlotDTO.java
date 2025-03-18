package com.heatguideIOT.demo.dto;

import java.time.LocalDateTime;

public class HeatGuideSlotDTO {
    private String lot;
    private String ferth;
    private String itemCheck;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;

    // Constructors

    public HeatGuideSlotDTO(String lot,String ferth ,String itemCheck, LocalDateTime startTime, LocalDateTime finishTime) {
        this.lot = lot;
        this.ferth = ferth;
        this.itemCheck = itemCheck;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    // Getters and Setters
    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getItemCheck() {
        return itemCheck;
    }

    public void setItemCheck(String itemCheck) {
        this.itemCheck = itemCheck;
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

    public String getFerth() {
        return ferth;
    }

    public void setFerth(String ferth) {
        this.ferth = ferth;
    }
}
