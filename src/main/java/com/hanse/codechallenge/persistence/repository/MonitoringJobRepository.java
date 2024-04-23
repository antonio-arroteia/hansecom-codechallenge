package com.hanse.codechallenge.persistence.repository;

import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonitoringJobRepository extends JpaRepository<PersistedMonitoringJob, Long> {

    Optional<PersistedMonitoringJob> findTopByJobName(String jobName);

}
