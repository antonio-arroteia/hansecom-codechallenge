package com.hanse.codechallenge.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.Instant;
import java.util.List;

@Data
@Schema(description = "Monitor Job DTO")
public class MonitoringJobDTO {

    @NotBlank @NotNull
    @Schema(description = "Name of the job")
    private String jobName;
    @NotBlank @NotNull
    @Schema(description = "Result of the job")
    private String url;
    @NotNull
    @Schema(description = "Interval between job executions")
    private long intervalInSeconds;
    @Schema (description = "Job results")
    @Null
    private List<JobResultDTO> results;
    @Null
    @Schema(description = "Creation Date")
    private Instant createdDate;

    public MonitoringJobDTO(String jobName, String url, long intervalInSeconds, Instant createdDate, List<JobResultDTO> results) {
        this.jobName = jobName;
        this.url = url;
        this.intervalInSeconds = intervalInSeconds;
        this.createdDate = createdDate;
        this.results = results;
    }

    public MonitoringJobDTO() {

    }
}
