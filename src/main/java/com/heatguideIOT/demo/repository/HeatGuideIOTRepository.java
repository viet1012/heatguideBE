package com.heatguideIOT.demo.repository;

import com.heatguideIOT.demo.dto.MachineDashboardDTO;
import com.heatguideIOT.demo.model.HeatGuideIOT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeatGuideIOTRepository extends JpaRepository<HeatGuideIOT, Integer> { // 🔹 Sửa Long thành Integer



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
                    d.Qty,
                    d.FERTH,
                    COALESCE(iot.machine, d.ITEMCHECK) AS machine,
                    d.ITEMCHECK,
                    iot.NOTE,
                    MIN(d.STARTTIME) AS STARTTIME,
                    MAX(d.FINISHTIME) AS FINISHTIME,
                    ROW_NUMBER() OVER (
                        PARTITION BY l.lot, d.FERTH, d.ITEMCHECK
                        ORDER BY MAX(d.FINISHTIME) DESC
                    ) AS rn
                FROM RankedLots AS l
                INNER JOIN F2_HeatGuide_Daily AS d
                    ON l.POREQNO = d.POREQNO
                LEFT JOIN F2_HeatGuide_IOTData iot
                    ON d.POREQNO = iot.POREQNO AND d.ITEMCHECK = iot.ITEMCHECK
                LEFT JOIN F2_HeatGuide_Daily d2
                    ON d.POREQNO = d2.POREQNO AND d2.ITEMCHECK IN ('Heat Finish', 'Waiting')
                LEFT JOIN HeatFinishGuide hfg
                    ON hfg.PO = l.POREQNO
                WHERE
                    d.FERTH NOT IN ('Mold Post', 'Main Post')
                    AND d.STARTTIME >= DATEADD(DAY, -7, GETDATE())
                    AND d2.POREQNO IS NULL
                    AND hfg.PO IS NULL
                    AND l.lot_rank = 1
                GROUP BY
                    l.lot, d.POREQNO, iot.machine, d.FERTH, d.ITEMCHECK, d.Qty, iot.NOTE
            )
            SELECT
                lot,
                FERTH,
                ITEMCHECK,
                machine,
                STARTTIME,
                FINISHTIME,
                POREQNO,
                Qty,
            
                -- NOTE chỉ khi là HRC
                CASE
                    WHEN ITEMCHECK LIKE 'HRC%' THEN NOTE
                    ELSE NULL
                END AS NOTE,
            
                -- ✅ Thêm itemCheckFinal: nếu là HRC_1 hoặc HRC_2 thì giữ nguyên, còn lại NULL
                CASE
                    WHEN ITEMCHECK IN ('HRC_1', 'HRC_2') THEN ITEMCHECK
                    ELSE NULL
                END AS itemCheckFinal
            
            FROM RankedData
            WHERE rn = 1
            ORDER BY lot ASC, STARTTIME ASC
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
                    d.Qty,
                    d.FERTH,
                    d.ITEMCHECK,
                    COALESCE(iot.machine, d.ITEMCHECK) AS machine,
                    iot.NOTE,
                    MIN(d.STARTTIME) AS STARTTIME,
                    MAX(d.FINISHTIME) AS FINISHTIME,
                    ROW_NUMBER() OVER (
                        PARTITION BY l.lot, d.FERTH, d.ITEMCHECK
                        ORDER BY MAX(d.FINISHTIME) DESC
                    ) AS rn
                FROM RankedLots AS l
                INNER JOIN F2_HeatGuide_Daily AS d
                    ON l.POREQNO = d.POREQNO
                LEFT JOIN F2_HeatGuide_IOTData iot
                    ON d.POREQNO = iot.POREQNO AND d.ITEMCHECK = iot.ITEMCHECK
                LEFT JOIN F2_HeatGuide_Daily d2
                    ON d.POREQNO = d2.POREQNO AND d2.ITEMCHECK = 'Waiting'
                LEFT JOIN HeatFinishGuide hfg
                    ON hfg.PO = l.POREQNO
                WHERE
                    d.FERTH IN ('Mold Bush', 'Main Bush', 'Sub Post', 'Sub Bush', 'Dowel Pins')
                    AND d.STARTTIME >= DATEADD(DAY, -10, GETDATE())
                    AND hfg.PO IS NULL
                    AND d2.ITEMCHECK = 'Waiting'
                    AND l.lot_rank = 1
                GROUP BY
                    l.lot, d.POREQNO, d.FERTH, d.ITEMCHECK, iot.machine, d.Qty, iot.NOTE
            ),
            FinalData AS (
                SELECT
                    lot,
                    FERTH,
                    ITEMCHECK,
                    machine,
                    STARTTIME,
                    FINISHTIME,
                    POREQNO,
                    Qty,
                    -- NOTE chỉ khi là HRC
                    CASE
                        WHEN ITEMCHECK LIKE 'HRC%' THEN NOTE
                        ELSE NULL
                    END AS NOTE,
            
                    -- itemCheckFinal chỉ khi là HRC_1 hoặc HRC_2
                    CASE
                        WHEN ITEMCHECK IN ('HRC_1', 'HRC_2') THEN ITEMCHECK
                        ELSE NULL
                    END AS itemCheckFinal,
            
                    ROW_NUMBER() OVER (PARTITION BY lot, FERTH, ITEMCHECK ORDER BY STARTTIME DESC) AS rn
                FROM RankedData
            )
            SELECT
                lot,
                FERTH,
                ITEMCHECK,
                machine,
                STARTTIME,
                FINISHTIME,
                POREQNO,
                Qty,
                NOTE,
                itemCheckFinal
            FROM FinalData
            WHERE rn = 1
            ORDER BY lot ASC, STARTTIME ASC
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
                    d.Qty,
                    d.FERTH,
                    d.ITEMCHECK,
                    COALESCE(iot.machine, d.ITEMCHECK) AS machine,  -- ✅ Ưu tiên machine, nếu NULL thì lấy ITEMCHECK
                    iot.NOTE,
                    MIN(d.STARTTIME) AS STARTTIME,
                    MAX(d.FINISHTIME) AS FINISHTIME,
                    ROW_NUMBER() OVER (
                        PARTITION BY l.lot, d.FERTH, d.ITEMCHECK
                        ORDER BY MAX(d.FINISHTIME) DESC
                    ) AS rn
                FROM RankedLots AS l
                INNER JOIN F2_HeatGuide_Daily d
                    ON l.POREQNO = d.POREQNO
                LEFT JOIN F2_HeatGuide_IOTData iot
                    ON d.POREQNO = iot.POREQNO
                    AND d.ITEMCHECK = iot.ITEMCHECK
                LEFT JOIN F2_HeatGuide_Daily d2
                    ON d.POREQNO = d2.POREQNO
                    AND d2.ITEMCHECK IN ('Heat Finish', 'Waiting')
                LEFT JOIN HeatFinishGuide hfg
                    ON hfg.PO = l.POREQNO
                WHERE
                    d.STARTTIME >= DATEADD(DAY, -7, GETDATE())
                    AND d2.POREQNO IS NULL
                    AND hfg.PO IS NULL
                    AND l.lot_rank = 1
                GROUP BY
                    l.lot, d.POREQNO, d.Qty, d.FERTH, d.ITEMCHECK, iot.machine, iot.NOTE
            )
            SELECT
                lot,
                FERTH,
                ITEMCHECK,
                machine,
                STARTTIME,
                FINISHTIME,
                POREQNO,
                Qty,
            
                -- ✅ NOTE chỉ hiển thị nếu là HRC
                CASE
                    WHEN ITEMCHECK LIKE 'HRC%' THEN NOTE
                    ELSE NULL
                END AS NOTE,
            
                -- ✅ itemCheckFinal chỉ lấy 'HRC_1' hoặc 'HRC_2'
                CASE
                    WHEN ITEMCHECK IN ('HRC_1', 'HRC_2') THEN ITEMCHECK
                    ELSE NULL
                END AS itemCheckFinal
            
            FROM RankedData
            WHERE rn = 1
            ORDER BY lot ASC, STARTTIME ASC
            OPTION (HASH JOIN, RECOMPILE);
            
            
            
            """, nativeQuery = true)
    List<Object[]> findDailyHeatGuideMainAndMoldIOT();

}
