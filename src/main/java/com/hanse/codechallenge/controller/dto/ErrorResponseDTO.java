package com.hanse.codechallenge.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@JsonSerialize
@Schema(description = "Error Response")
public class ErrorResponseDTO {
    @Schema(description = "Error status")
    private final int status;
    @Schema(description = "Error name")
    private final String error;
    @Schema(description = "Error message")
    private final String message;

    public ErrorResponseDTO(HttpStatus status, String message) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
    }
}