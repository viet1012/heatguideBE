package com.heatguideIOT.demo.dto;


import java.math.BigDecimal;
import java.util.Date;

public class MachineDashboardDTO {
    private String macId;
    private String macName;
    private BigDecimal stdHour;
    private Integer stdOutputDay;
    private String finishDate;
    private Integer outputQty;
    private BigDecimal processingHour;
    private BigDecimal finishTime;

    public MachineDashboardDTO(String macId, String macName, BigDecimal stdHour, Integer stdOutputDay,
                               String finishDate, Integer outputQty, BigDecimal processingHour,
                               BigDecimal finishTime) {
        this.macId = macId;
        this.macName = macName;
        this.stdHour = stdHour;
        this.stdOutputDay = stdOutputDay;
        this.finishDate = finishDate;
        this.outputQty = outputQty;
        this.processingHour = processingHour;
        this.finishTime  = finishTime;
    }
    public MachineDashboardDTO(String macId, String macName, BigDecimal stdHour, Integer stdOutputDay,
                               String finishDate, Integer outputQty, BigDecimal processingHour
                              ) {
        this.macId = macId;
        this.macName = macName;
        this.stdHour = stdHour;
        this.stdOutputDay = stdOutputDay;
        this.finishDate = finishDate;
        this.outputQty = outputQty;
        this.processingHour = processingHour;
    }
    // Getters & Setters
    public String getMacId() {
        return macId;
    }

    public void setMacId(String macId) {
        this.macId = macId;
    }

    public String getMacName() {
        return macName;
    }

    public void setMacName(String macName) {
        this.macName = macName;
    }

    public BigDecimal getStdHour() {
        return stdHour;
    }

    public void setStdHour(BigDecimal stdHour) {
        this.stdHour = stdHour;
    }

    public Integer getStdOutputDay() {
        return stdOutputDay;
    }

    public void setStdOutputDay(Integer stdOutputDay) {
        this.stdOutputDay = stdOutputDay;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getOutputQty() {
        return outputQty;
    }

    public void setOutputQty(Integer outputQty) {
        this.outputQty = outputQty;
    }

    public BigDecimal getProcessingHour() {
        return processingHour;
    }

    public void setProcessingHour(BigDecimal processingHour) {
        this.processingHour = processingHour;
    }
}
