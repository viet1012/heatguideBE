package com.heatguideIOT.demo.controller;


import com.heatguideIOT.demo.dto.HeatGuideOutputDTO;
import com.heatguideIOT.demo.dto.MachineDashboardDTO;
import com.heatguideIOT.demo.dto.MachineSummaryDTO;
import com.heatguideIOT.demo.model.HeatGuideIOT;
import com.heatguideIOT.demo.service.HeatGuideIOTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/heatguide")
public class HeatGuideIOTController {
    @Autowired
    private HeatGuideIOTService service;

    @GetMapping()
    public List<HeatGuideIOT> getAll() {
        return service.findAllRecords();
    }

    @GetMapping("/machines")
//    public List<MachineSummaryDTO> getMachineSummary() {
//        return service.getMachineSummary();
//    }
    public List<MachineDashboardDTO> getMachineSummary() {
        return service.getMachineDashboardData();
    }

    @GetMapping("/by-day-range")
    public List<HeatGuideOutputDTO> getDataByTimeRangeAndItemCheck(@RequestParam String itemCheck) {
        return service.getDataByTimeRangeAndItemCheck(itemCheck);
    }

    @GetMapping("/by-night-range")
    public List<HeatGuideOutputDTO> getDataNightShiftDataByItem(@RequestParam String itemCheck) {
        return service.getDataNightShiftDataByItem(itemCheck);
    }


    @GetMapping("/by-night-yesterday-range")
    public List<HeatGuideOutputDTO> findNightShiftDataByItemForYesterday(@RequestParam String itemCheck) {
        return service.findNightShiftDataByItemForYesterday(itemCheck);
    }

//    @GetMapping("/by-time-range")
//    public List<HeatGuideOutputDTO> getDataByTimeRangeAndItemCheck() {
//        return service.getDataByTimeRangeAndItemCheck();
//    }

}
