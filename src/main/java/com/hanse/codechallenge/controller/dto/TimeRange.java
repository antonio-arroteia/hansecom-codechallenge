package com.hanse.codechallenge.controller.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class TimeRange {
    private Instant lowerBound;
    private Instant upperBound;

    public TimeRange(Instant lowerBound, Instant upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
}