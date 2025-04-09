package com.heatguideIOT.demo.controller;

import com.heatguideIOT.demo.model.BatchAbnormal;
import com.heatguideIOT.demo.service.BatchAbnormalService;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/heatguide")
public class BatchAbnormalController {

    @Autowired
    private BatchAbnormalService batchService;

    @PostMapping("/lot_abnormal/add")
    public BatchAbnormal addBatch(@RequestBody BatchAbnormal batch) {
        return batchService.addBatch(batch);
    }
}
