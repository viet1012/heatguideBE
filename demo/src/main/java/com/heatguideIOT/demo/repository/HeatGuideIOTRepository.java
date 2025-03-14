package com.heatguideIOT.demo.repository;

import com.heatguideIOT.demo.dto.HeatGuideOutputDTO;
import com.heatguideIOT.demo.dto.MachineDashboardDTO;
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

//    @Query(value = """
//            WITH CTE AS (
//                SELECT
//                    ITEMCHECK,
//                    no_id,
//                    machine,
//                    STARTTIME,
//                    FINISHTIME,
//                    SUM(Qty) AS Sum_Qty,
//                    ROW_NUMBER() OVER (PARTITION BY ITEMCHECK, no_id, machine ORDER BY STARTTIME) AS RowNum
//                FROM F2_HeatGuide_IOTData
//                WHERE no_id IS NOT NULL
//                AND FINISHTIME IS NOT NULL
//                GROUP BY ITEMCHECK, no_id, machine, STARTTIME, FINISHTIME
//            )
//            SELECT
//                machine,
//                ITEMCHECK,
//                SUM(ROUND(DATEDIFF(MINUTE, FirstOfSTARTTIME, FirstOfFINISHTIME) / 60.0, 2)) AS TTL_Time,
//                SUM(Sum_Qty) AS Quantity
//            FROM (
//                SELECT
//                    ITEMCHECK,
//                    no_id,
//                    machine,
//                    STARTTIME AS FirstOfSTARTTIME,
//                    FINISHTIME AS FirstOfFINISHTIME,
//                    Sum_Qty
//                FROM CTE
//                WHERE RowNum = 1
//            ) AS Table_Temp
//            GROUP BY machine, ITEMCHECK;
//    """, nativeQuery = true)
//    List<MachineSummaryDTO> findMachineSummary();


    @Query(value = """
        SELECT dash.Mac_ID, dash.Mac_Name, dash.STD_Hour, STD_Output_Day, output.Finish_Date, output.OutputQty, processing.Processing_Hour
                FROM dbo.F2_Dashboard_Heat_Guide dash
                \s
                LEFT JOIN\s
                (
                    SELECT machine, Finish_Date, SUM(Sum_Qty) AS OutputQty
                    FROM
                    (
                        SELECT iot.machine,\s
                               CONVERT(varchar, DATEADD(hour, -6, iot.FINISHTIME), 101) AS Finish_Date,\s
                               SUM(iot.Qty) AS Sum_Qty
                        FROM dbo.F2_HeatGuide_IOTData iot
                        WHERE iot.FINISHTIME IS NOT NULL
                        GROUP BY iot.machine, CONVERT(varchar, DATEADD(hour, -6, iot.FINISHTIME), 101)
                    ) AS Table_Temp
                    GROUP BY machine, Finish_Date
                    HAVING Finish_Date = CONVERT(varchar, DATEADD(hour, -6, GETDATE()), 101)
                ) AS output
                ON dash.Mac_ID = output.machine
                \s
                LEFT JOIN\s
                (
                    SELECT Table_Temp2.machine,\s
                           CAST(DATEDIFF(MINUTE, Table_Temp2.FirstOfSTARTTIME, GETDATE()) / 60.0 AS DECIMAL(10,2))  AS Processing_Hour
                    FROM
                    (
                        SELECT iot.machine,\s
                               MIN(iot.no_id) AS FirstOfno_id,\s
                               MIN(iot.STARTTIME) AS FirstOfSTARTTIME
                        FROM dbo.F2_HeatGuide_IOTData iot
                        WHERE iot.FINISHTIME IS NULL
                        GROUP BY iot.machine
                    ) AS Table_Temp2
                ) AS processing
                ON dash.Mac_ID = processing.machine;
    """, nativeQuery = true)
    List<MachineDashboardDTO> getMachineDashboardData();


//    @Query(value = """
//    SELECT
//        DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0) AS hourSlot,
//        SUM(h.Qty) AS totalQty,
//        h.ITEMCHECK
//    FROM F2_HeatGuide_IOTData h
//    WHERE h.STARTTIME >= DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
//      AND h.STARTTIME < DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
//      AND h.ITEMCHECK = :itemCheck
//    GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0), h.ITEMCHECK
//    ORDER BY hourSlot
//    """, nativeQuery = true)
//    List<Object[]> findHourlyDataByItem(@Param("itemCheck") String itemCheck);
@Query(value = """
        SELECT\s
           DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0) AS hourSlot,\s
           SUM(h.Qty) AS totalQty,
           d.Mac_ID
       FROM F2_HeatGuide_IOTData h
       JOIN F2_Dashboard_Heat_Guide d ON h.machine = d.Mac_ID
       WHERE h.STARTTIME >= DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
         AND h.STARTTIME < DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
         AND d.Mac_ID = :macID  -- Lọc theo Mac_ID
       GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0), d.Mac_ID
       ORDER BY hourSlot;
   
    """, nativeQuery = true)
List<Object[]> findHourlyDataByItem(@Param("macID") String itemCheck);


//    @Query(value = """
//    SELECT
//        DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0) AS hourSlot,
//        SUM(h.Qty) AS totalQty,
//        h.ITEMCHECK
//    FROM F2_HeatGuide_IOTData h
//    WHERE h.STARTTIME >= DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
//      AND h.STARTTIME < DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE() + 1)))
//      AND h.ITEMCHECK = :itemCheck
//    GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0), h.ITEMCHECK
//    ORDER BY hourSlot
//    """, nativeQuery = true)
//    List<Object[]> findNightShiftDataByItem(@Param("itemCheck") String itemCheck);


        @Query(value = """
                SELECT\s
                   DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0) AS hourSlot,\s
                   SUM(h.Qty) AS totalQty,
                   d.Mac_ID
               FROM F2_HeatGuide_IOTData h
               JOIN F2_Dashboard_Heat_Guide d ON h.machine = d.Mac_ID
             WHERE h.STARTTIME >= DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
                     AND h.STARTTIME < DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE() + 1)))
                 AND d.Mac_ID = :macID  -- Lọc theo Mac_ID
               GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0), d.Mac_ID
               ORDER BY hourSlot;
    """, nativeQuery = true)
    List<Object[]> findNightShiftDataByItem(@Param("macID") String itemCheck);

    @Query(value = """
              SELECT\s
                   DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0) AS hourSlot,\s
                   SUM(h.Qty) AS totalQty,
                   d.Mac_ID
               FROM F2_HeatGuide_IOTData h
               JOIN F2_Dashboard_Heat_Guide d ON h.machine = d.Mac_ID
               WHERE h.STARTTIME >= DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE() - 2)))
                      AND h.STARTTIME < DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE() - 1)))
                 AND d.Mac_ID = :macID  -- Lọc theo Mac_ID
               GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0), d.Mac_ID
               ORDER BY hourSlot;
    """, nativeQuery = true)
    List<Object[]> findNightShiftDataByItemForYesterday(@Param("macID") String itemCheck);

}
