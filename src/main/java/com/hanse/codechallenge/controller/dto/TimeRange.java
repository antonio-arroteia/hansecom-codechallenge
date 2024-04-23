package com.hanse.codechallenge.controller.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class TimeRange {
    private Instant fromInstant;
    private Instant untilInstant;

    public TimeRange(Instant fromInstant, Instant untilInstant) {
        this.fromInstant = fromInstant;
        this.untilInstant = untilInstant;
    }
}