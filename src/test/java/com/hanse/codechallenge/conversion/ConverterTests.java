package com.hanse.codechallenge.conversion;

import com.hanse.codechallenge.controller.dto.JobResultDTO;
import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.enums.Result;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringResult;
import com.hanse.codechallenge.utils.conversion.DtoToMonitoringJobConverter;
import com.hanse.codechallenge.utils.conversion.MonitoringJobResultToDtoConverter;
import com.hanse.codechallenge.utils.conversion.MonitoringJobToDtoConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConverterTests {

    @Test
    public void testDtoToMonitoringJobConverter() {

        MonitoringJobDTO dto = new MonitoringJobDTO("Job Name", "http://example.com", 60, null, null);
        DtoToMonitoringJobConverter converter = new DtoToMonitoringJobConverter();

        PersistedMonitoringJob job = converter.convert(dto);

        Assertions.assertEquals(dto.getJobName(), job.getJobName());
        Assertions.assertEquals(dto.getUrl(), job.getUrl());
        Assertions.assertEquals(dto.getIntervalInSeconds(), job.getIntervalInSeconds());
    }

    @Test
    public void testMonitoringJobResultToDtoConverter() {

        PersistedMonitoringResult result = new PersistedMonitoringResult(Result.SUCCESS, 100, "OK", "Additional info");
        MonitoringJobResultToDtoConverter converter = new MonitoringJobResultToDtoConverter();

        JobResultDTO dto = converter.convert(result);

        Assertions.assertEquals(result.getResult(), dto.getResult());
        Assertions.assertEquals(result.getResponseTimeInMs(), dto.getResponseTime());
        Assertions.assertEquals(result.getStatusName(), dto.getStatusName());
        Assertions.assertEquals(result.getInfo(), dto.getInfo());
    }

    @Test
    public void testMonitoringJobToDtoConverter() {

        PersistedMonitoringJob job = new PersistedMonitoringJob(1L, "Job Name", "http://example.com", 60, null);
        MonitoringJobToDtoConverter converter = new MonitoringJobToDtoConverter();

        MonitoringJobDTO dto = converter.convert(job);

        Assertions.assertEquals(job.getJobName(), dto.getJobName());
        Assertions.assertEquals(job.getUrl(), dto.getUrl());
        Assertions.assertEquals(job.getIntervalInSeconds(), dto.getIntervalInSeconds());
    }
}
