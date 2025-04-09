package com.heatguideIOT.demo.repository;

import com.heatguideIOT.demo.model.BatchAbnormal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchAbnormalRepository extends JpaRepository<BatchAbnormal, Long> {
}
