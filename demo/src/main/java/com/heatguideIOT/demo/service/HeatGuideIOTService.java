package com.heatguideIOT.demo.service;

import com.heatguideIOT.demo.dto.*;
import com.heatguideIOT.demo.model.HeatGuideIOT;
import com.heatguideIOT.demo.repository.HeatGuideIOTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HeatGuideIOTService {
    @Autowired
    private HeatGuideIOTRepository repository;

    public List<HeatGuideIOT> findAllRecords() {
        return repository.findAllRecords();
    }

//    public List<MachineSummaryDTO> getMachineSummary() {
//        return repository.findMachineSummary();
//    }

    public List<MachineDashboardDTO> getMachineDashboardData() {
        return repository.getMachineDashboardData();
    }

    public List<HeatGuideOutputDTO> getDataByTimeRangeAndItemCheck( String itemCheck) {
        List<Object[]> results = repository.findHourlyDataByItem(itemCheck);

        System.out.println("Query Params: itemCheck = " + itemCheck);
        System.out.println("Raw Query Results: " + results.size());
        for (Object[] row : results) {
            System.out.println("starttime: " + row[0] + ", qty: " + row[1] + ", itemcheck: " + row[2]);
        }

        return results.stream().map(obj ->
                new HeatGuideOutputDTO(
                        ((Timestamp) obj[0]).toLocalDateTime(), // starttime
                        (Integer) obj[1],        // qty
                        (String) obj[2]          // itemcheck
                )
        ).collect(Collectors.toList());
    }

    public List<HeatGuideOutputDTO> getDataNightShiftDataByItem( String itemCheck) {
        List<Object[]> results = repository.findNightShiftDataByItem(itemCheck);

        System.out.println("Query Params: itemCheck = " + itemCheck);
        System.out.println("Raw Query Results: " + results.size());
        for (Object[] row : results) {
            System.out.println("starttime: " + row[0] + ", qty: " + row[1] + ", itemcheck: " + row[2]);
        }

        return results.stream().map(obj ->
                new HeatGuideOutputDTO(
                        ((Timestamp) obj[0]).toLocalDateTime(), // starttime
                        (Integer) obj[1],        // qty
                        (String) obj[2]          // itemcheck
                )
        ).collect(Collectors.toList());
    }

    public List<HeatGuideOutputDTO> findNightShiftDataByItemForYesterday( String itemCheck) {
        List<Object[]> results = repository.findNightShiftDataByItemForYesterday(itemCheck);

        for (Object[] row : results) {
            System.out.println("starttime: " + row[0] + ", qty: " + row[1] + ", itemcheck: " + row[2]);
        }

        return results.stream().map(obj ->
                new HeatGuideOutputDTO(
                        ((Timestamp) obj[0]).toLocalDateTime(), // starttime
                        (Integer) obj[1],        // qty
                        (String) obj[2]          // itemcheck
                )
        ).collect(Collectors.toList());
    }




    public List<FerthDTO> findDailyHeatGuideMoldAndMainIOT() {
        List<Object[]> results = repository.findDailyHeatGuideMoldAndMainIOT();

        // Debug: In dữ liệu raw từ database
        for (Object[] row : results) {
            System.out.println("lot: " + row[0] + ", ferth: " + row[1] + ", itemcheck: " + row[2] +
                    ", starttime: " + row[3] + ", finishtime: " + row[4]);
        }

        // Convert kết quả query thành danh sách HeatGuideSlotDTO
        List<HeatGuideSlotDTO> slots = results.stream().map(obj ->
                new HeatGuideSlotDTO(
                        (String) obj[0], // lot
                        (String) obj[1], // ferth
                        (String) obj[2], // itemCheck
                        obj[3] != null ? ((Timestamp) obj[3]).toLocalDateTime() : null, // startTime
                        obj[4] != null ? ((Timestamp) obj[4]).toLocalDateTime() : null  // finishTime
                )
        ).collect(Collectors.toList());

        // Nhóm dữ liệu theo ferth, sau đó theo lot
        Map<String, Map<String, List<HeatGuideItemDTO>>> groupedData = slots.stream()
                .collect(Collectors.groupingBy(
                        HeatGuideSlotDTO::getFerth,
                        Collectors.groupingBy(
                                HeatGuideSlotDTO::getLot,
                                Collectors.mapping(slot -> new HeatGuideItemDTO(
                                        slot.getItemCheck(),
                                        slot.getStartTime(),
                                        slot.getFinishTime()
                                ), Collectors.toList())
                        )
                ));

        // Chuyển đổi Map thành danh sách FerthDTO
        List<FerthDTO> ferthList = groupedData.entrySet().stream()
                .map(entry -> new FerthDTO(
                        entry.getKey(),
                        entry.getValue().entrySet().stream()
                                .map(lotEntry -> new LotDTO(
                                        lotEntry.getKey(),
                                        lotEntry.getValue()
                                ))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());

        return ferthList;
    }

    public List<FerthDTO> findDailyHeatGuideSubAndDowelIOT() {
        List<Object[]> results = repository.findDailyHeatGuideSubAndDowelIOT();

        // Debug: In dữ liệu raw từ database
        for (Object[] row : results) {
            System.out.println("lot: " + row[0] + ", ferth: " + row[1] + ", itemcheck: " + row[2] +
                    ", starttime: " + row[3] + ", finishtime: " + row[4]);
        }

        // Convert kết quả query thành danh sách HeatGuideSlotDTO
        List<HeatGuideSlotDTO> slots = results.stream().map(obj ->
                new HeatGuideSlotDTO(
                        (String) obj[0], // lot
                        (String) obj[1], // ferth
                        (String) obj[2], // itemCheck
                        obj[3] != null ? ((Timestamp) obj[3]).toLocalDateTime() : null, // startTime
                        obj[4] != null ? ((Timestamp) obj[4]).toLocalDateTime() : null  // finishTime
                )
        ).collect(Collectors.toList());

        // Nhóm dữ liệu theo ferth, sau đó theo lot
        Map<String, Map<String, List<HeatGuideItemDTO>>> groupedData = slots.stream()
                .collect(Collectors.groupingBy(
                        HeatGuideSlotDTO::getFerth,
                        Collectors.groupingBy(
                                HeatGuideSlotDTO::getLot,
                                Collectors.mapping(slot -> new HeatGuideItemDTO(
                                        slot.getItemCheck(),
                                        slot.getStartTime(),
                                        slot.getFinishTime()
                                ), Collectors.toList())
                        )
                ));

        // Chuyển đổi Map thành danh sách FerthDTO
        List<FerthDTO> ferthList = groupedData.entrySet().stream()
                .map(entry -> new FerthDTO(
                        entry.getKey(),
                        entry.getValue().entrySet().stream()
                                .map(lotEntry -> new LotDTO(
                                        lotEntry.getKey(),
                                        lotEntry.getValue()
                                ))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());

        return ferthList;
    }

    public List<FerthDTO> findDailyHeatGuideIOT(String type) {
        List<Object[]> results;

        if ("MoldAndMain".equals(type)) {
            results = repository.findDailyHeatGuideMoldAndMainIOT();
        } else if ("SubAndDowel".equals(type)) {
            results = repository.findDailyHeatGuideSubAndDowelIOT();
        } else if ("MainAndMold".equals(type)) {
            results = repository.findDailyHeatGuideMainAndMoldIOT();
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        // Debug: In dữ liệu raw từ database
        results.forEach(row -> System.out.println(
                "lot: " + row[0] + ", ferth: " + row[1] + ", itemcheck: " + row[2] +
                        ", starttime: " + row[3] + ", finishtime: " + row[4]
        ));

        // Chuyển đổi danh sách Object[] thành danh sách HeatGuideSlotDTO
        List<HeatGuideSlotDTO> slots = results.stream()
                .map(obj -> new HeatGuideSlotDTO(
                        (String) obj[0], // lot
                        (String) obj[1], // ferth
                        (String) obj[2], // itemCheck
                        obj[3] != null ? ((Timestamp) obj[3]).toLocalDateTime() : null, // startTime
                        obj[4] != null ? ((Timestamp) obj[4]).toLocalDateTime() : null  // finishTime
                ))
                .collect(Collectors.toList());

        // Nhóm dữ liệu theo ferth và lot
        Map<String, Map<String, List<HeatGuideItemDTO>>> groupedData = slots.parallelStream()
                .collect(Collectors.groupingBy(
                        HeatGuideSlotDTO::getFerth,
                        Collectors.groupingBy(
                                HeatGuideSlotDTO::getLot,
                                Collectors.mapping(slot -> new HeatGuideItemDTO(
                                        slot.getItemCheck(),
                                        slot.getStartTime(),
                                        slot.getFinishTime()
                                ), Collectors.toList())
                        )
                ));

        // Chuyển đổi Map thành danh sách FerthDTO
        return groupedData.entrySet().parallelStream()
                .map(entry -> new FerthDTO(
                        entry.getKey(),
                        entry.getValue().entrySet().stream()
                                .map(lotEntry -> new LotDTO(
                                        lotEntry.getKey(),
                                        lotEntry.getValue()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

}