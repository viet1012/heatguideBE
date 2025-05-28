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


    /// QUERY ĐÚNG
//    @Query(value = """
//        SELECT dash.Mac_ID, dash.Mac_Name, dash.STD_Hour, STD_Output_Day,
//               output.Finish_Date, output.OutputQty,
//               processing.Processing_Hour,
//                processing.LastFinishTime,
//               processing.Note -- ✅ Thêm Note vào SELECT
//        FROM dbo.F2_Dashboard_Heat_Guide dash
//
//        -- Join với Output Data
//        LEFT JOIN
//        (
//            SELECT machine, Finish_Date, SUM(Sum_Qty) AS OutputQty
//            FROM
//            (
//                SELECT iot.machine,
//                       CONVERT(varchar, DATEADD(hour, -6, iot.FINISHTIME), 101) AS Finish_Date,
//                       SUM(iot.Qty) AS Sum_Qty
//                FROM dbo.F2_HeatGuide_IOTData iot
//                WHERE iot.FINISHTIME IS NOT NULL
//                GROUP BY iot.machine, CONVERT(varchar, DATEADD(hour, -6, iot.FINISHTIME), 101)
//            ) AS Table_Temp
//            GROUP BY machine, Finish_Date
//            HAVING Finish_Date = CONVERT(varchar, DATEADD(hour, -6, GETDATE()), 101)
//        ) AS output
//        ON dash.Mac_ID = output.machine
//
//        -- Join với Processing Data
//        LEFT JOIN
//        (
//            SELECT Table_Temp2.machine,
//                   CAST(DATEDIFF(MINUTE, MIN(Table_Temp2.FirstOfSTARTTIME), GETDATE()) / 60.0 AS DECIMAL(10,2))  AS Processing_Hour,
//                   MAX(iot.FINISHTIME) AS LastFinishTime,
//                   MAX(iot.Note) AS Note -- ✅ Lấy Note (nếu có nhiều, lấy MAX để tránh GROUP BY lỗi)
//            FROM
//            (
//                SELECT iot.machine,
//                       MIN(iot.no_id) AS FirstOfno_id,
//                       MIN(iot.STARTTIME) AS FirstOfSTARTTIME
//                FROM dbo.F2_HeatGuide_IOTData iot
//                WHERE iot.FINISHTIME IS NULL
//                GROUP BY iot.machine
//            ) AS Table_Temp2
//            LEFT JOIN dbo.F2_HeatGuide_IOTData iot
//            ON Table_Temp2.machine = iot.machine
//            GROUP BY Table_Temp2.machine
//        ) AS processing
//        ON dash.Mac_ID = processing.machine;
//    """, nativeQuery = true)
//    List<MachineDashboardDTO> getMachineDashboardData();

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
                       WHERE h.STARTTIME >= DATEADD(HOUR, 18, CONVERT(DATETIME, CONVERT(DATE, GETDATE() - 1)))
                              AND h.STARTTIME < DATEADD(HOUR, 6, CONVERT(DATETIME, CONVERT(DATE, GETDATE())))
                         AND d.Mac_ID = :macID  -- Lọc theo Mac_ID
                       GROUP BY DATEADD(HOUR, DATEDIFF(HOUR, 0, h.STARTTIME), 0), d.Mac_ID
                       ORDER BY hourSlot;
            """, nativeQuery = true)
    List<Object[]> findNightShiftDataByItemForYesterday(@Param("macID") String itemCheck);


    @Query(value = """
                    WITH RankedLots AS (
                        SELECT
                            lot,
                            POREQNO,
                            ROW_NUMBER() OVER (PARTITION BY POREQNO ORDER BY lot ASC) AS lot_rank
                        FROM F2_HeatGuide_Lot
                    ),
                    RankedData AS (
                        SELECT
                            l.lot,
                            d.POREQNO,
                            d.FERTH,
                            COALESCE(iot.machine, d.ITEMCHECK) AS machine, -- Nếu machine NULL thì lấy ITEMCHECK
                            d.ITEMCHECK,
                            MIN(d.STARTTIME) AS STARTTIME,
                            MAX(d.FINISHTIME) AS FINISHTIME,
                            ROW_NUMBER() OVER (PARTITION BY l.lot, d.FERTH, d.ITEMCHECK ORDER BY MAX(d.FINISHTIME) DESC) AS rn
                        FROM RankedLots AS l
                        INNER JOIN F2_HeatGuide_Daily AS d
                            ON l.POREQNO = d.POREQNO
                        LEFT JOIN F2_HeatGuide_IOTData iot
                            ON d.POREQNO = iot.POREQNO
                            AND d.ITEMCHECK = iot.ITEMCHECK -- Thêm điều kiện để lấy đúng machine
                        LEFT JOIN F2_HeatGuide_Daily d2
                            ON d.POREQNO = d2.POREQNO
                            AND d2.ITEMCHECK IN ('Heat Finish', 'Waiting')
                        LEFT JOIN HeatFinishGuide hfg
                            ON hfg.PO = l.POREQNO
                        WHERE
                            d.FERTH IN ('Mold Bush', 'Main Bush', 'Sub Post', 'Sub Bush', 'Dowel Pins')
                            AND d.STARTTIME >= DATEADD(DAY, -7, GETDATE())
                            AND d2.POREQNO IS NULL
                            AND hfg.PO IS NULL
                            AND l.lot_rank = 1  -- Chỉ lấy lot nhỏ nhất cho mỗi POREQNO
                        GROUP BY
                            l.lot, d.POREQNO, iot.machine, d.FERTH, d.ITEMCHECK
                    )
                    SELECT
                        lot,
                        FERTH,
                        ITEMCHECK,
                        machine,
                        STARTTIME,
                        FINISHTIME
                    FROM RankedData
                    WHERE rn = 1  -- Chỉ lấy 1 dòng duy nhất cho mỗi nhóm ITEMCHECK
                    ORDER BY
                        lot ASC, STARTTIME ASC
                    OPTION (HASH JOIN, RECOMPILE);
            
            """, nativeQuery = true)
    List<Object[]> findDailyHeatGuideMoldAndMainIOT();

    @Query(value = """
              WITH RankedLots AS (
                          SELECT
                              lot,
                              POREQNO,
                              ROW_NUMBER() OVER (PARTITION BY POREQNO ORDER BY lot ASC) AS lot_rank
                          FROM F2_HeatGuide_Lot
                      ),
                      RankedData AS (
                          SELECT
                              l.lot,
                              d.POREQNO,
                              d.FERTH,
                              d.ITEMCHECK,
                              COALESCE(iot.machine, d.ITEMCHECK) AS machine,  -- Nếu machine NULL thì lấy ITEMCHECK
                              MIN(d.STARTTIME) AS STARTTIME,
                              MAX(d.FINISHTIME) AS FINISHTIME
                          FROM RankedLots AS l
                          INNER JOIN F2_HeatGuide_Daily AS d
                              ON l.POREQNO = d.POREQNO
                          LEFT JOIN F2_HeatGuide_IOTData iot
                              ON d.POREQNO = iot.POREQNO
                              AND d.ITEMCHECK = iot.ITEMCHECK  -- Điều kiện lấy đúng machine
                          LEFT JOIN F2_HeatGuide_Daily d2
                              ON d.POREQNO = d2.POREQNO
                              AND d2.ITEMCHECK = 'Waiting'  -- Chỉ lấy các mục có 'Waiting'
                          LEFT JOIN HeatFinishGuide hfg
                              ON hfg.PO = l.POREQNO
                          WHERE
                              d.FERTH IN ('Mold Bush', 'Main Bush', 'Sub Post', 'Sub Bush', 'Dowel Pins')
                              AND d.STARTTIME >= DATEADD(DAY, -10, GETDATE())
                              AND hfg.PO IS NULL  -- Không có trong bảng HeatFinishGuide
                              AND l.lot_rank = 1  -- Chỉ lấy lot nhỏ nhất cho mỗi POREQNO
                              AND d2.ITEMCHECK = 'Waiting'
                          GROUP BY
                              l.lot, d.POREQNO, d.FERTH, d.ITEMCHECK, iot.machine
                      ),
                      FinalData AS (
                          SELECT
                              lot, FERTH, ITEMCHECK, machine, STARTTIME, FINISHTIME,
                              ROW_NUMBER() OVER (PARTITION BY lot, FERTH, ITEMCHECK ORDER BY STARTTIME DESC) AS rn
                          FROM RankedData
                      )
                      SELECT
                          lot, FERTH, ITEMCHECK, machine, STARTTIME, FINISHTIME
                      FROM FinalData
                      WHERE rn = 1  -- Chỉ lấy dòng mới nhất cho mỗi nhóm ITEMCHECK
                      ORDER BY
                          lot ASC, STARTTIME ASC
                      OPTION (HASH JOIN, RECOMPILE);
            
            """, nativeQuery = true)
    List<Object[]> findDailyHeatGuideMoldAndMainWaitingIOT();

    @Query(value = """    
                 SELECT\s
                     l.lot,   \s
                     d.FERTH,   \s
                     d.ITEMCHECK,   \s
                     MIN(d.STARTTIME) AS STARTTIME,    \s
                     MAX(d.FINISHTIME) AS FINISHTIME\s
                 FROM\s
                     F2_HeatGuide_Lot AS l\s
                 INNER JOIN\s
                     F2_HeatGuide_Daily AS d\s
                     ON l.poreqno = d.POREQNO\s
                 WHERE\s
                      d.FERTH IN ('Sub Post', 'Sub Bush', 'Dowel Pins')
                     AND d.STARTTIME >= DATEADD(DAY, -3, GETDATE())\s
                     AND d.ITEMCHECK <> 'Heat Finish'
                     AND NOT EXISTS (
                         SELECT 1\s
                         FROM HeatFinishGuide hfg\s
                         WHERE hfg.PO = l.poreqno
                     )
                 GROUP BY\s
                     l.lot, d.FERTH, d.ITEMCHECK
                 ORDER BY\s
                     l.lot ASC, STARTTIME ASC;
            """, nativeQuery = true)
    List<Object[]> findDailyHeatGuideSubAndDowelIOT();

    @Query(value = """
             WITH RankedLots AS (
                         SELECT
                             lot,
                             POREQNO,
                             ROW_NUMBER() OVER (PARTITION BY POREQNO ORDER BY lot ASC) AS lot_rank
                         FROM F2_HeatGuide_Lot
                     ),
                     RankedData AS (
                         SELECT
                             l.lot,
                             d.POREQNO,
                             d.FERTH,
                             COALESCE(iot.machine, d.ITEMCHECK) AS machine, -- ✅ Lấy machine từ iot, nếu NULL thì lấy ITEMCHECK
                             d.ITEMCHECK,
                             MIN(d.STARTTIME) AS STARTTIME,
                             MAX(d.FINISHTIME) AS FINISHTIME,
                             ROW_NUMBER() OVER (PARTITION BY l.lot, d.FERTH, d.ITEMCHECK ORDER BY MAX(d.FINISHTIME) DESC) AS rn
                         FROM RankedLots AS l
                         INNER JOIN F2_HeatGuide_Daily AS d
                             ON l.POREQNO = d.POREQNO
                         LEFT JOIN F2_HeatGuide_IOTData iot
                             ON d.POREQNO = iot.POREQNO
                             AND d.ITEMCHECK = iot.ITEMCHECK -- ✅ Thêm điều kiện để lấy đúng machine
                         LEFT JOIN F2_HeatGuide_Daily d2
                             ON d.POREQNO = d2.POREQNO
                             AND d2.ITEMCHECK IN ('Heat Finish', 'Waiting')
                         LEFT JOIN HeatFinishGuide hfg
                             ON hfg.PO = l.POREQNO
                         WHERE
                             d.FERTH IN ('Mold Post', 'Main Post') -- ✅ Lọc theo Mold Post & Main Post
                             AND d.STARTTIME >= DATEADD(DAY, -7, GETDATE())
                             AND d2.POREQNO IS NULL
                             AND hfg.PO IS NULL
                             AND l.lot_rank = 1  -- ✅ Chỉ lấy lot nhỏ nhất cho mỗi POREQNO
                         GROUP BY
                             l.lot, d.POREQNO, d.FERTH, d.ITEMCHECK, iot.machine
                     )
                     SELECT
                         lot, FERTH, ITEMCHECK, machine, STARTTIME, FINISHTIME -- ✅ Thêm machine vào kết quả
                     FROM RankedData
                     WHERE rn = 1  -- ✅ Chỉ lấy 1 dòng duy nhất cho mỗi nhóm ITEMCHECK
                     ORDER BY
                         lot ASC, STARTTIME ASC
                     OPTION (HASH JOIN, RECOMPILE);
            
            
            """, nativeQuery = true)
    List<Object[]> findDailyHeatGuideMainAndMoldIOT();

}
