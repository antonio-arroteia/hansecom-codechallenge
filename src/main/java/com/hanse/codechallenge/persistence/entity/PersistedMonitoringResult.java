package com.hanse.codechallenge.persistence.entity;

import com.hanse.codechallenge.enums.Result;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.Instant;

@Data
@Embeddable
public class PersistedMonitoringResult {
    @Enumerated(EnumType.STRING)
    private Result result;
    private long responseTimeInMs;
    private String statusName;
    private String info;
    private Instant creationDate = Instant.now();

    public PersistedMonitoringResult(Result result, long responseTimeInMs, String statusName, String info) {
        this.result = result;
        this.responseTimeInMs = responseTimeInMs;
        this.statusName = statusName;
        this.info = info;
    }

    public PersistedMonitoringResult() {

    }
}