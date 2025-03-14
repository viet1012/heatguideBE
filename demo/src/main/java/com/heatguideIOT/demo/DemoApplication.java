package com.heatguideIOT.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

//@Query(value = """
////            WITH CTE AS (
////                SELECT
////                    ITEMCHECK,
////                    no_id,
////                    machine,
////                    STARTTIME,
////                    FINISHTIME,
////                    SUM(Qty) AS Sum_Qty,
////                    ROW_NUMBER() OVER (PARTITION BY ITEMCHECK, no_id, machine ORDER BY STARTTIME) AS RowNum
////                FROM F2_HeatGuide_IOTData
////                WHERE no_id IS NOT NULL
////                AND FINISHTIME IS NOT NULL
////                GROUP BY ITEMCHECK, no_id, machine, STARTTIME, FINISHTIME
////            )
////            SELECT
////                machine,
////                ITEMCHECK,
////                SUM(ROUND(DATEDIFF(MINUTE, FirstOfSTARTTIME, FirstOfFINISHTIME) / 60.0, 2)) AS TTL_Time,
////                SUM(Sum_Qty) AS Quantity
////            FROM (
////                SELECT
////                    ITEMCHECK,
////                    no_id,
////                    machine,
////                    STARTTIME AS FirstOfSTARTTIME,
////                    FINISHTIME AS FirstOfFINISHTIME,
////                    Sum_Qty
////                FROM CTE
////                WHERE RowNum = 1
////            ) AS Table_Temp
////            GROUP BY machine, ITEMCHECK;
////    """, nativeQuery = true)
//    List<MachineSummaryDTO> findMachineSummary();
