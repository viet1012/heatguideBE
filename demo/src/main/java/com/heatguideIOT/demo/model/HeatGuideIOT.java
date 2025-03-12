package com.heatguideIOT.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "F2_HeatGuide_IOTData")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HeatGuideIOT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;  // ✅ Đúng kiểu dữ liệu `int identity`

    @Column(name = "WorkerID", length = 50, nullable = true)
    private String workerId;  // ✅ Đổi từ `Long` sang `String` vì là `nvarchar(50)`

    @Column(name = "POREQNO", length = 50, nullable = true)
    private String poreqno;  // ✅ `nvarchar(50)`

    @Column(name = "Qty", nullable = true)
    private Integer qty;  // ✅ `int`

    @Column(name = "FERTH", length = 50, nullable = true)
    private String ferth;  // ✅ `nvarchar(50)`

    @Column(name = "ITEMCHECK", length = 50, nullable = true)
    private String itemcheck;  // ✅ `nvarchar(50)`, không phải `Boolean`

    @Column(name = "STARTTIME", nullable = true)
    private LocalDateTime starttime;  // ✅ `datetime`

    @Column(name = "FINISHTIME", nullable = true)
    private LocalDateTime finishtime;  // ✅ `datetime`

    @Column(name = "time", length = 50, nullable = true)
    private String time;  // ✅ `nvarchar(50)`, không phải `Long`

    @Column(name = "status", length = 50, nullable = true)
    private String status;  // ✅ `nvarchar(50)`

    @Column(name = "Note", length = 50, nullable = true)
    private String note;  // ✅ `nvarchar(50)`

    @Column(name = "name_product", length = 50, nullable = true)
    private String nameProduct;  // ✅ `nvarchar(50)`, đổi tên biến cho chuẩn Java

    @Column(name = "Iot_id", length = 50, nullable = true)
    private String Iot_id;  // ✅ `nvarchar(50)`, đổi tên biến cho chuẩn Java

    @Column(name = "machine", length = 50, nullable = true)
    private String machine;  // ✅ `nvarchar(50)`, đổi tên biến cho chuẩn Java

    @Column(name = "no_id", length = 50, nullable = true)
    private String noId;  // ✅ `nvarchar(50)`, đổi tên biến cho chuẩn Java

}
