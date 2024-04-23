package com.hanse.codechallenge.controller.dto;

import com.hanse.codechallenge.enums.Result;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
@Schema(description = "Job Result DTO")
public class JobResultDTO {

    @Schema(description = "Result: SUCCESS, FAILED")
    private Result result;

    @Schema(description = "Response time from the execution")
    private long responseTime;

    @Schema(description = "Result HTTP status")
    private String statusName;

    @Schema(description = "Result additional info")
    private String info;

    @Schema(description = "Creation date")
    private Instant creationDate;

    public JobResultDTO(Result result, long responseTime, String statusName, String info, Instant creationDate) {
        this.result = result;
        this.responseTime = responseTime;
        this.statusName = statusName;
        this.info = info;
        this.creationDate = creationDate;
    }
}
