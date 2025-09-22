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
            ),
            -- Lấy Qty của từng POREQNO
            POQtyData AS (
                SELECT
                    POREQNO,
                    MAX(Qty) AS PO_Qty,
                    MAX(FERTH) AS FERTH
                FROM F2_HeatGuide_Daily
                WHERE STARTTIME >= DATEADD(DAY, -7, GETDATE())
                GROUP BY POREQNO
            ),
            BatchPOs AS (
                SELECT
                    b.batchid,
                    CAST(STRING_AGG(CAST(b.poreqno AS VARCHAR(MAX)), ', ') AS VARCHAR(MAX)) AS POREQNOs_in_same_lot,
                    CAST(STRING_AGG(
                        'POREQNO: ' + CAST(b.poreqno AS VARCHAR(MAX)) +
                        ' | FERTH: ' + ISNULL(pq.FERTH, 'N/A') +
                        ' | QTY: ' + CAST(ISNULL(pq.PO_Qty, 0) AS VARCHAR(MAX)),
                        ' ; '
                    ) AS VARCHAR(MAX)) AS POREQNOs_with_qty
                FROM F2_HeatGuide_Batch b
                LEFT JOIN POQtyData pq ON b.poreqno = pq.POREQNO
                GROUP BY b.batchid
            )
            
            SELECT
                r.lot,
                r.FERTH,
                r.ITEMCHECK,
                r.machine,
                r.STARTTIME,
                r.FINISHTIME,
                r.POREQNO,
                r.Qty,
                CASE
                    WHEN r.ITEMCHECK LIKE 'HRC%' THEN r.NOTE
                    ELSE NULL
                END AS NOTE,
                CASE
                    WHEN r.ITEMCHECK IN ('HRC_1', 'HRC_2') THEN r.ITEMCHECK
                    ELSE NULL
                END AS itemCheckFinal,
                b.POREQNOs_with_qty,
                mb.RFID_Key   -- ✅ lấy thêm RFID_Key
            FROM RankedData r
            LEFT JOIN BatchPOs b ON r.lot = b.batchid
            LEFT JOIN F2_HeatGuide_Master_Batch mb ON b.batchid = mb.Batch_id   -- ✅ join bảng master
            WHERE r.rn = 1
            ORDER BY r.lot ASC, r.STARTTIME ASC
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
    List<Object[]> findDailyHeatGuideMoldAndMainIOT1();

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
                    COALESCE(iot.machine, d.ITEMCHECK) AS machine,  -- ✅ ưu tiên machine, nếu NULL thì lấy ITEMCHECK
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
                r.lot,
                r.FERTH,
                r.ITEMCHECK,
                r.machine,
                r.STARTTIME,
                r.FINISHTIME,
                r.POREQNO,
                r.Qty,
            
                -- ✅ NOTE chỉ hiển thị nếu là HRC
                CASE
                    WHEN r.ITEMCHECK LIKE 'HRC%' THEN r.NOTE
                    ELSE NULL
                END AS NOTE,
            
                -- ✅ itemCheckFinal chỉ lấy 'HRC_1' hoặc 'HRC_2'
                CASE
                    WHEN r.ITEMCHECK IN ('HRC_1', 'HRC_2') THEN r.ITEMCHECK
                    ELSE NULL
                END AS itemCheckFinal,
                NULL AS POREQNOs_with_qty,   -- ✅ thêm dummy column
                mb.RFID_Key   -- ✅ thêm RFID từ bảng master batch
            FROM RankedData r
            LEFT JOIN F2_HeatGuide_Master_Batch mb
                ON r.lot = mb.Batch_id   -- ✅ join để lấy RFID_Key
            WHERE r.rn = 1
            ORDER BY r.lot ASC, r.STARTTIME ASC
            OPTION (HASH JOIN, RECOMPILE);
            
            """, nativeQuery = true)
    List<Object[]> findDailyHeatGuideMainAndMoldIOT();

}
