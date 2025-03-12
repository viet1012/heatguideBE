package com.heatguideIOT.demo.repository;

import com.heatguideIOT.demo.dto.HeatGuideOutputDTO;
import com.heatguideIOT.demo.dto.MachineSummaryDTO;
import com.heatguideIOT.demo.model.HeatGuideIOT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HeatGuideIOTRepository extends JpaRepository<HeatGuideIOT, Integer> { // 🔹 Sửa Long thành Integer

    @Query(value = "SELECT * FROM F2_HeatGuide_IOTData", nativeQuery = true)
    List<HeatGuideIOT> findAllRecords();

    @Query(value = "SELECT TOP 22 * FROM F2_HeatGuide_IOTData", nativeQuery = true)
    List<HeatGuideIOT> findTop22Records(); // 🔹 Sửa lại tên method cho đúng với TOP 22

    @Query(value = """
            WITH CTE AS (
                SELECT
                    ITEMCHECK,
                    no_id,
                    machine,
                    STARTTIME,
                    FINISHTIME,
                    SUM(Qty) AS Sum_Qty,
                    ROW_NUMBER() OVER (PARTITION BY ITEMCHECK, no_id, machine ORDER BY STARTTIME) AS RowNum
                FROM F2_HeatGuide_IOTData
                WHERE no_id IS NOT NULL
                AND FINISHTIME IS NOT NULL
                GROUP BY ITEMCHECK, no_id, machine, STARTTIME, FINISHTIME
            )
            SELECT
                machine,
                ITEMCHECK,
                SUM(ROUND(DATEDIFF(MINUTE, FirstOfSTARTTIME, FirstOfFINISHTIME) / 60.0, 2)) AS TTL_Time,
                SUM(Sum_Qty) AS Quantity
            FROM (
                SELECT
                    ITEMCHECK,
                    no_id,
                    machine,
                    STARTTIME AS FirstOfSTARTTIME,
                    FINISHTIME AS FirstOfFINISHTIME,
                    Sum_Qty
                FROM CTE
                WHERE RowNum = 1
            ) AS Table_Temp
            GROUP BY machine, ITEMCHECK;
    """, nativeQuery = true)
    List<MachineSummaryDTO> findMachineSummary();


    @Query(value = """
SELECT h.STARTTIME AS starttime,-- Trả về kiểu LocalDateTime
           SUM(h.Qty) AS totalQty,
           h.ITEMCHECK
    FROM F2_HeatGuide_IOTData h
    WHERE h.STARTTIME >= DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
      AND h.STARTTIME < DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
      AND h.ITEMCHECK = 'Cool_Fan_1'
    GROUP BY h.STARTTIME, h.ITEMCHECK
    ORDER BY starttime;
    """, nativeQuery = true)
    List<Object[]> findHourlyDataByItem();


    @Query(value = """
    SELECT
        DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0) AS hourSlot, 
        SUM(h.Qty) AS totalQty,
        h.ITEMCHECK
    FROM F2_HeatGuide_IOTData h
    WHERE h.STARTTIME >= DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
      AND h.STARTTIME < DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
      AND h.ITEMCHECK = :itemCheck
    GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0), h.ITEMCHECK
    ORDER BY hourSlot
    """, nativeQuery = true)
    List<Object[]> findHourlyDataByItem(@Param("itemCheck") String itemCheck);

    @Query(value = """
    SELECT
        DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0) AS hourSlot,
        SUM(h.Qty) AS totalQty,
        h.ITEMCHECK
    FROM F2_HeatGuide_IOTData h
    WHERE h.STARTTIME >= DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
      AND h.STARTTIME < DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE() + 1)))
      AND h.ITEMCHECK = :itemCheck
    GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0), h.ITEMCHECK
    ORDER BY hourSlot
    """, nativeQuery = true)
    List<Object[]> findNightShiftDataByItem(@Param("itemCheck") String itemCheck);

    @Query(value = """
    SELECT
        DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0) AS hourSlot,
        SUM(h.Qty) AS totalQty,
        h.ITEMCHECK
    FROM F2_HeatGuide_IOTData h
    WHERE h.STARTTIME >= DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE() - 1))) 
      AND h.STARTTIME < DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
      AND h.ITEMCHECK = :itemCheck
    GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0), h.ITEMCHECK
    ORDER BY hourSlot
    """, nativeQuery = true)
    List<Object[]> findNightShiftDataByItemForYesterday(@Param("itemCheck") String itemCheck);

}
