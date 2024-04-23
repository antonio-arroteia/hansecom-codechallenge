package com.hanse.codechallenge.controller.dto;

import com.hanse.codechallenge.enums.Result;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Range;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@Schema(description = "DTO for filtering job search criteria")
public class JobResultSearchCriteriaDTO {

    @Schema(description = "Name of the job")
    private String jobName;

    @Schema(description = "Result of the job")
    private Result result;

    @Schema(description = "HTTP status of the job")
    private HttpStatus status;

    @Schema(description = "Time range for the job")
    private TimeRange timeRange;

}
