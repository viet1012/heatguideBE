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


    public List<FerthDTO> findDailyHeatGuideIOT(String type) {
        List<Object[]> results;

        // Lấy dữ liệu raw từ repository theo type
        switch (type) {
            case "MoldAndMain":
                results = repository.findDailyHeatGuideMoldAndMainIOT();
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

                    // ✅ Tạo infoMap: gom info theo ferth + poreqno + qty
                    Map<String, LotInfoDTO> infoMap = IntStream.range(0, results.size())
                            .filter(i -> ((String) results.get(i)[0]).equals(lot)) // chỉ dòng thuộc lot hiện tại
                            .mapToObj(i -> {
                                Object[] row = results.get(i);
                                String ferth = (String) row[1];
                                String poreqno = row[6] != null ? row[6].toString() : null;
                                Integer qty = row[7] != null ? ((Number) row[7]).intValue() : null;
                                String note = row[8] != null ? row[8].toString() : null;
                                String itemCheckFinal = row[9] != null ? row[9].toString().trim().toUpperCase() : "";
                                String poreqnosWithQty = (row.length > 10 && row[10] != null) ? row[10].toString() : null;
                                String key = ferth + "|" + poreqno + "|" + qty;

                                return Map.entry(key, new LotInfoDTO(itemCheckFinal, note, ferth, qty, poreqno, poreqnosWithQty));
                            })
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (existing, replacement) -> {
                                        // Ưu tiên giữ cái có itemCheckFinal
                                        if (replacement.getItemCheckFinal() != null && !replacement.getItemCheckFinal().isEmpty()) {
                                            return replacement;
                                        }
                                        return existing;
                                    },
                                    LinkedHashMap::new
                            ));

                    List<LotInfoDTO> infoList = new ArrayList<>(infoMap.values());

                    // Lấy unique itemCheck cho items
                    Map<String, HeatGuideItemDTO> uniqueItemCheckMap = lotItems.stream()
                            .collect(Collectors.toMap(
                                    HeatGuideItemDTO::getItemCheck,
                                    item -> item,
                                    (existing, replacement) -> existing,
                                    LinkedHashMap::new
                            ));

                    return new LotDTO(lot, infoList, new ArrayList<>(uniqueItemCheckMap.values()));
                })
                .collect(Collectors.toList());

        return List.of(new FerthDTO(lotList));
    }



}