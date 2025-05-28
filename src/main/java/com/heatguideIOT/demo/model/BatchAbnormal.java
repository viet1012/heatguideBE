package com.heatguideIOT.demo.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "F2_HeatGuide_Daily_Abnormal")
public class BatchAbnormal {

    @Id // ✅ Đặt batchId là khóa chính
    @Column(name = "batch_id", length = 50, nullable = false)
    private String batchId;

    @Column(name = "dateadd", length = 50, nullable = true)
    private LocalDateTime dateadd;

    @Column(name = "process", length = 50, nullable = false)
    private String process;

    @Column(name = "comment", length = 50, nullable = false)
    private String comment;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public LocalDateTime getDateadd() {
        return dateadd;
    }

    public void setDateadd(LocalDateTime dateadd) {
        this.dateadd = dateadd;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
