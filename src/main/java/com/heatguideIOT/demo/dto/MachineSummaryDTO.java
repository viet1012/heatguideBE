package com.heatguideIOT.demo.dto;


import java.math.BigDecimal;

public interface MachineSummaryDTO {
    String getMachine();
    String getItemCheck();
    BigDecimal getTtlTime();
    BigDecimal getQuantity();
}
