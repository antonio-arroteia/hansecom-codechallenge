package com.hanse.codechallenge.persistence.entity;

import com.hanse.codechallenge.enums.Result;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@Embeddable
public class PersistedMonitoringResult {
    private Result result;
    private long responseTime;
    private String statusName;
    private String info;
    private Instant creationDate = Instant.now();

    public PersistedMonitoringResult(Result result, long responseTime, String statusName, String info) {
        this.result = result;
        this.responseTime = responseTime;
        this.statusName = statusName;
        this.info = info;
    }

    public PersistedMonitoringResult() {

    }
}