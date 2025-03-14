package com.heatguideIOT.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "F2_Dashboard_Heat_Guide")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardHeatGuideIOT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;  // ✅ Đúng kiểu dữ liệu `int identity`

    @Column(name = "Mac_ID", length = 50, nullable = true)
    private String macID;  // ✅ Đổi từ `Long` sang `String` vì là `nvarchar(50)`

    @Column(name = "Mac_Name", length = 50, nullable = true)
    private String macName;  // ✅ `nvarchar(50)`

    @Column(name = "Group", nullable = true)
    private String group;  // ✅ `int`

    @Column(name = "STD_Hour", length = 50, nullable = true)
    private int stdHour;  // ✅ `nvarchar(50)`

    @Column(name = "STD_Output", length = 50, nullable = true)
    private int itemcheck;  // ✅ `nvarchar(50)`, không phải `Boolean`


}
