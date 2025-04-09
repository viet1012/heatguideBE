package com.heatguideIOT.demo.service;


import com.heatguideIOT.demo.model.BatchAbnormal;
import com.heatguideIOT.demo.repository.BatchAbnormalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BatchAbnormalService {

    @Autowired
    private BatchAbnormalRepository batchRepository;

    public BatchAbnormal addBatch(BatchAbnormal batch) {
        return batchRepository.save(batch);
    }
}
