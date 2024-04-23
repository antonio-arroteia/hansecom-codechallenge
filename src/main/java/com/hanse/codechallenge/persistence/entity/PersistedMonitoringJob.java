package com.hanse.codechallenge.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Entity(name = "monitoring_job")
public class PersistedMonitoringJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String jobName;
    private String url;
    private long intervalInSeconds;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "monitoring_job_result", joinColumns = @JoinColumn(name = "job_id"),
            indexes = {@Index(name = "job_result_index", columnList = "job_id, result")})
    private List<PersistedMonitoringResult> results = new ArrayList<>();
    private Instant creationDate = Instant.now();

    public PersistedMonitoringJob(Long id, String jobName, String url, long intervalInSeconds, List<PersistedMonitoringResult> results) {
        this.id = id;
        this.jobName = jobName;
        this.url = url;
        this.intervalInSeconds = intervalInSeconds;
        this.results = results;
    }

    public PersistedMonitoringJob() {}

    public void addResult(PersistedMonitoringResult result){
        if(this.results == null ) this.results = new ArrayList<>();
        this.results.add(result); // Now you can directly add elements to the list
    }
}