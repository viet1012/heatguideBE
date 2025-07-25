package com.heatguideIOT.demo.service;

import com.heatguideIOT.demo.dto.*;
import com.heatguideIOT.demo.model.HeatGuideIOT;
import com.heatguideIOT.demo.repository.HeatGuideIOTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class HeatGuideIOTService {
    @Autowired
    private HeatGuideIOTRepository repository;

    public List<HeatGuideIOT> findAllRecords() {
        return repository.findAllRecords();
    }

    public List<MachineDashboardDTO> getMachineDashboardData() {
        return repository.getMachineDashboardData();
    }

    public List<HeatGuideOutputDTO> getDataByTimeRangeAndItemCheck(String itemCheck) {
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

    public List<HeatGuideOutputDTO> getDataNightShiftDataByItem(String itemCheck) {
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

    public List<HeatGuideOutputDTO> findNightShiftDataByItemForYesterday(String itemCheck) {
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

    public List<FerthDTO> findDailyHeatGuideIOT(String type) {
        List<Object[]> results;

        // Lấy dữ liệu raw từ repository theo type
        switch (type) {
            case "MoldAndMain":
                results = repository.findDailyHeatGuideMoldAndMainIOT();
                break;
            case "SubAndDowel":
                results = repository.findDailyHeatGuideSubAndDowelIOT();
                break;
            case "MainAndMold":
                results = repository.findDailyHeatGuideMainAndMoldIOT();
                break;
            case "MoldAndMainWaiting":
                results = repository.findDailyHeatGuideMoldAndMainWaitingIOT();
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }

        // Convert Object[] to HeatGuideItemDTO
        List<HeatGuideItemDTO> items = results.stream()
                .map(row -> new HeatGuideItemDTO(
                        (String) row[2], // itemCheck
                        row[4] != null ? ((Timestamp) row[4]).toLocalDateTime() : null, // startTime
                        row[5] != null ? ((Timestamp) row[5]).toLocalDateTime() : null, // finishTime
                        row[3] != null ? (String) row[3] : null, // machine
                        row[7] != null ? ((Number) row[7]).intValue() : null, // qty
                        row[6] != null ? (String) row[6] : null, // poreqno
                        (String) row[1] // ferth
                )).collect(Collectors.toList());

        // Group by lot
        Map<String, List<HeatGuideItemDTO>> lotMap = IntStream.range(0, results.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        i -> (String) results.get(i)[0], // lot
                        LinkedHashMap::new,
                        Collectors.mapping(i -> items.get(i), Collectors.toList())
                ));

        // Convert to LotDTO
        List<LotDTO> lotList = lotMap.entrySet().stream()
                .map(entry -> {
                    String lot = entry.getKey();
                    List<HeatGuideItemDTO> lotItems = entry.getValue();

                    // Tạo danh sách info không trùng ferth + poreqno + qty
                    List<LotInfoDTO> infoList = lotItems.stream()
                            .map(item -> new LotInfoDTO(item.getFerth(), item.getQty(), item.getPOREQNO()))
                            .distinct()
                            .collect(Collectors.toList());

                    // Giữ lại itemCheck duy nhất (ví dụ lấy item đầu tiên với mỗi itemCheck)
                    Map<String, HeatGuideItemDTO> uniqueItemCheckMap = lotItems.stream()
                            .collect(Collectors.toMap(
                                    HeatGuideItemDTO::getItemCheck,
                                    item -> item,
                                    (existing, replacement) -> existing, // Giữ lại cái đầu tiên nếu trùng itemCheck
                                    LinkedHashMap::new // Giữ thứ tự
                            ));

                    // Trả kết quả
                    return new LotDTO(lot, infoList, new ArrayList<>(uniqueItemCheckMap.values()));
                })
                .collect(Collectors.toList());

        return List.of(new FerthDTO(lotList));
    }

    public List<FerthDTO> findDailyHeatGuideIOT1(String type) {
        List<Object[]> results;

        // Lấy dữ liệu raw từ repository theo type
        switch (type) {
            case "MoldAndMain":
                results = repository.findDailyHeatGuideMoldAndMainIOT();
                break;
            case "SubAndDowel":
                results = repository.findDailyHeatGuideSubAndDowelIOT();
                break;
            case "MainAndMold":
                results = repository.findDailyHeatGuideMainAndMoldIOT();
                break;
            case "MoldAndMainWaiting":
                results = repository.findDailyHeatGuideMoldAndMainWaitingIOT();
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }

        // Debug dữ liệu raw
        results.forEach(row -> System.out.printf(
                "lot: %s, ferth: %s, itemcheck: %s, machine: %s, starttime: %s, finishtime: %s, poreqno: %s, qty: %s%n",
                row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7]
        ));

        // Chuyển Object[] -> HeatGuideSlotDTO
        List<HeatGuideSlotDTO> slots = results.stream()
                .map(row -> {
                    String lot = (String) row[0];
                    String ferth = (String) row[1];
                    String itemCheck = (String) row[2];
                    String machine = row[3] != null ? (String) row[3] : null;
                    LocalDateTime startTime = row[4] != null ? ((Timestamp) row[4]).toLocalDateTime() : null;
                    LocalDateTime finishTime = row[5] != null ? ((Timestamp) row[5]).toLocalDateTime() : null;
                    String poreqno = row[6] != null ? (String) row[6] : null;
                    Integer qty = row[7] != null ? ((Number) row[7]).intValue() : null;

                    List<LotInfoDTO> infoList = new ArrayList<>();
                    infoList.add(new LotInfoDTO(ferth, qty, poreqno));

                    return new HeatGuideSlotDTO(lot, ferth,itemCheck, machine, startTime, finishTime, poreqno,qty);
                })
                .collect(Collectors.toList());


        // Nhóm theo ferth -> lot -> danh sách item
        Map<String, Map<String, List<HeatGuideItemDTO>>> grouped = slots.stream()
                .collect(Collectors.groupingBy(
                        HeatGuideSlotDTO::getFerth,
                        Collectors.groupingBy(
                                HeatGuideSlotDTO::getLot,
                                Collectors.mapping(slot -> new HeatGuideItemDTO(
                                        slot.getItemCheck(),
                                        slot.getStartTime(),
                                        slot.getFinishTime(),
                                        slot.getMachine(),
                                        slot.getQty(),
                                        slot.getPOREQNO(),
                                        slot.getFerth()
                                ), Collectors.toList())
                        )
                ));

        // Chuyển sang FerthDTO
        return grouped.entrySet().stream()
                .map(ferthEntry -> {
                    String ferth = ferthEntry.getKey();
                    Map<String, List<HeatGuideItemDTO>> lotMap = ferthEntry.getValue();

                    List<LotDTO> lotList = lotMap.entrySet().stream()
                            .map(lotEntry -> {
                                String lot = lotEntry.getKey();
                                List<HeatGuideItemDTO> items = lotEntry.getValue();

                                List<LotInfoDTO> infoList = items.stream()
                                        .map(item -> new LotInfoDTO(item.getFerth(), item.getQty(), item.getPOREQNO()))
                                        .distinct()
                                        .collect(Collectors.toList());

                                return new LotDTO(lot, infoList, items);
                            })
                            .collect(Collectors.toList());

                    return new FerthDTO( lotList);
                })
                .collect(Collectors.toList());
    }


//    public List<FerthDTO> findDailyHeatGuideIOT1(String type) {
//        List<Object[]> results;
//
//        if ("MoldAndMain".equals(type)) {
//            results = repository.findDailyHeatGuideMoldAndMainIOT();
//        } else if ("SubAndDowel".equals(type)) {
//            results = repository.findDailyHeatGuideSubAndDowelIOT();
//        } else if ("MainAndMold".equals(type)) {
//            results = repository.findDailyHeatGuideMainAndMoldIOT();
//        } else if ("MoldAndMainWaiting".equals(type)) {
//            results = repository.findDailyHeatGuideMoldAndMainWaitingIOT();
//        } else {
//            throw new IllegalArgumentException("Invalid type: " + type);
//        }
//
//        // Debug: In dữ liệu raw từ database
//        results.forEach(row -> System.out.println(
//                "lot: " + row[0] + ", ferth: " + row[1] + ", itemcheck: " + row[2] +
//                        ", machine: " + row[3] + ", starttime: " + row[4] + ", finishtime: " + row[5] +
//                        " obj[6]: " +  row[6] +    " obj[7]: " +  row[7]
//        ));
//
//        // Chuyển đổi danh sách Object[] thành danh sách HeatGuideSlotDTO
//        List<HeatGuideSlotDTO> slots = results.stream()
//                .map(obj -> new HeatGuideSlotDTO(
//                        (String) obj[0], // lot
//                        (String) obj[1], // ferth
//                        (String) obj[2], // itemCheck
//                        obj[3] != null ? (String) obj[3] : null, // machine
//                        obj[4] != null ? ((Timestamp) obj[4]).toLocalDateTime() : null, // startTime
//                        obj[5] != null ? ((Timestamp) obj[5]).toLocalDateTime() : null,
//                        obj[6] != null ? (String) obj[6] : null,// machine
//                        obj[7] != null ? (Integer) obj[7] : null// machine
//                        // finishTime
//
//                ))
//                .collect(Collectors.toList());
//
//        // Nhóm dữ liệu theo ferth và lot
//        Map<String, Map<String, List<HeatGuideItemDTO>>> groupedData = slots.parallelStream()
//                .collect(Collectors.groupingBy(
//                        HeatGuideSlotDTO::getFerth,
//                        Collectors.groupingBy(
//                                HeatGuideSlotDTO::getLot,
//                                Collectors.mapping(slot -> new HeatGuideItemDTO(
//                                        slot.getItemCheck(),
//                                        slot.getStartTime(),
//                                        slot.getFinishTime(),
//                                        slot.getMachine()  ,
//                                        slot.getQty(),
//                                        slot.getPOREQNO(),
//                                        slot.getFerth()
//                                ), Collectors.toList())
//                        )
//                ));
//
//        return groupedData.entrySet().parallelStream()
//                .map(entry -> {
//                    String ferth = entry.getKey(); // key của ferth
//                    Map<String, List<HeatGuideItemDTO>> lotsMap = entry.getValue();
//
//                    List<LotDTO> lotList = lotsMap.entrySet().stream()
//                            .map(lotEntry -> {
//                                String lot = lotEntry.getKey();
//                                List<HeatGuideItemDTO> items = lotEntry.getValue();
//
//                                // Gom nhiều info theo từng POREQNO riêng biệt
//                                List<LotInfoDTO> infoList = items.stream()
//                                        .map(i -> new LotInfoDTO(ferth, i.getQty(), i.getPOREQNO()))
//                                        .distinct() // để tránh lặp lại PO giống nhau
//                                        .collect(Collectors.toList());
//
//                                return new LotDTO(lot, infoList, items);
//                            })
//                            .collect(Collectors.toList());
//
//
//                    return new FerthDTO(ferth, lotList);
//
//                })
//                .collect(Collectors.toList());
//
//
//    }

}