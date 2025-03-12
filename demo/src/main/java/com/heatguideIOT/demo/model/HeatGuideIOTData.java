package com.heatguideIOT.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "F2_HeatGuide_I_ot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HeatGuideIOTData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;  // ✅ Đúng kiểu dữ liệu `int identity`

    @Column(name = "poreqno", length = 50, nullable = true)
    private String poreqno;  // ✅ `nvarchar(50)`

    @Column(name = "Iot", nullable = true)
    private Long qty;  // ✅ `int`

    @Column(name = "dateadd", nullable = true)
    private LocalDateTime dateadd;  // ✅ `datetime`

}
