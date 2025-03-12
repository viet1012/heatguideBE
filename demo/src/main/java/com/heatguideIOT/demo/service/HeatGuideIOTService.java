package com.heatguideIOT.demo.service;

import com.heatguideIOT.demo.dto.HeatGuideOutputDTO;
import com.heatguideIOT.demo.dto.MachineSummaryDTO;
import com.heatguideIOT.demo.model.HeatGuideIOT;
import com.heatguideIOT.demo.repository.HeatGuideIOTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HeatGuideIOTService {
    @Autowired
    private HeatGuideIOTRepository repository;

    public List<HeatGuideIOT> findAllRecords() {
        return repository.findAllRecords();
    }

    public List<MachineSummaryDTO> getMachineSummary() {
        return repository.findMachineSummary();
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


}