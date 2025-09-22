package com.heatguideIOT.demo.controller;


import com.heatguideIOT.demo.dto.*;
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

    @GetMapping("/findDailyHeatGuideMoldAndMainIOT")
    public List<FerthDTO> findDailyHeatGuideMoldAndMainIOT() {
        List<FerthDTO> findDailyHeatGuideMoldAndMainIOT =  service.findDailyHeatGuideIOT("MoldAndMain");
        int totalLots = findDailyHeatGuideMoldAndMainIOT.stream()
                .mapToInt(ferth -> ferth.getLots().size())
                .sum();

        System.out.println("Tổng số lots: " + totalLots);

        return service.findDailyHeatGuideIOT("MoldAndMain");
    }

    @GetMapping("/findDailyHeatGuideMoldAndMainWaitingIOT")
    public List<FerthDTO> findDailyHeatGuideMoldAndMainWaitingIOT() {
        List<FerthDTO> findDailyHeatGuideMoldAndMainIOT =  service.findDailyHeatGuideIOT("MoldAndMainWaiting");
        int totalLots = findDailyHeatGuideMoldAndMainIOT.stream()
                .mapToInt(ferth -> ferth.getLots().size())
                .sum();

        System.out.println("Tổng số lots: " + totalLots);

        return service.findDailyHeatGuideIOT("MoldAndMainWaiting");
    }


    @GetMapping("/findDailyHeatGuideMainAndMoldIOT")
    public List<FerthDTO> findDailyHeatGuideMainAndMoldIOT() {
        List<FerthDTO> findDailyHeatGuideMoldAndMainIOT =  service.findDailyHeatGuideIOT("MainAndMold");
        int totalLots = findDailyHeatGuideMoldAndMainIOT.stream()
                .mapToInt(ferth -> ferth.getLots().size())
                .sum();
        System.out.println("Tổng số lots: " + totalLots);

        return service.findDailyHeatGuideIOT("MainAndMold");
    }


}
