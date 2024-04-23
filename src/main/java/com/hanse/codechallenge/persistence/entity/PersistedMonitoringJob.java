package com.hanse.codechallenge.persistence.entity;

import jakarta.persistence.*;
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

    @Column(name = "job_name")
    private String jobName;

    private String url;

    @Column(name = "interval_in_seconds")
    private long intervalInSeconds;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "monitoring_job_result", joinColumns = @JoinColumn(name = "job_id"))
    private List<PersistedMonitoringResult> results = new ArrayList<>();

    @Column(name = "creation_date")
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