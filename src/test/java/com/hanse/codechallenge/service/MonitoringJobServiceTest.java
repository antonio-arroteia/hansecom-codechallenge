package com.hanse.codechallenge.service;


import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import com.hanse.codechallenge.utils.validation.RequestBodyValidation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MonitoringJobServiceTest {

    @Mock
    private MonitoringJobExecutionService executionServiceMock;

    @Mock
    private MonitoringJobRepository jobRepositoryMock;

    @Mock
    private ConversionService conversionServiceMock;

    @InjectMocks
    private MonitoringJobService jobService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RequestBodyValidation.setJobRepository(jobRepositoryMock);
    }

    @Test
    public void testCreateMonitorJob() {

        MonitoringJobDTO monitoringJobDTO = new MonitoringJobDTO("TestJob", "https://example.com", 10, Instant.now(), null);
        PersistedMonitoringJob persistedMonitoringJob = new PersistedMonitoringJob(null,"TestJob", "https://example.com", 10, new ArrayList<>());
        when(jobRepositoryMock.count()).thenReturn(4L);
        when(conversionServiceMock.convert(any(), eq(PersistedMonitoringJob.class))).thenReturn(persistedMonitoringJob);
        when(jobRepositoryMock.save(any())).thenReturn(persistedMonitoringJob);

        PersistedMonitoringJob result = jobService.createMonitorJob(monitoringJobDTO);

        assertNotNull(result);
        assertEquals("TestJob", result.getJobName());
        assertEquals("https://example.com", result.getUrl());
        assertEquals(10, result.getIntervalInSeconds());
        verify(conversionServiceMock).convert(any(), eq(PersistedMonitoringJob.class));
        verify(jobRepositoryMock).save(persistedMonitoringJob);
        verify(executionServiceMock).updateTasks();
    }

    @Test
    public void testUpdateMonitoringJob() {

        MonitoringJobDTO monitoringJobDTO = new MonitoringJobDTO("TestJob", "https://example.com", 10, Instant.now(), null);
        PersistedMonitoringJob persistedMonitoringJob = new PersistedMonitoringJob(1L, "TestJob", "https://example.com", 10, new ArrayList<>());
        when(jobRepositoryMock.findTopByJobName(any())).thenReturn(Optional.of(persistedMonitoringJob));
        when(jobRepositoryMock.save(any())).thenReturn(persistedMonitoringJob);

        PersistedMonitoringJob result = jobService.updateMonitoringJob(monitoringJobDTO);

        assertNotNull(result);
        assertEquals("TestJob", result.getJobName());
        assertEquals("https://example.com", result.getUrl());
        assertEquals(10, result.getIntervalInSeconds());
        verify(jobRepositoryMock, times(2)).findTopByJobName(any());
        verify(jobRepositoryMock).save(persistedMonitoringJob);

    }

    @Test
    public void testGetMonitoringJobs() {

        List<PersistedMonitoringJob> jobs = new ArrayList<>();
        jobs.add(new PersistedMonitoringJob(1L, "TestJob1", "https://example.com/1", 10, new ArrayList<>()));
        jobs.add(new PersistedMonitoringJob(2L, "TestJob2", "https://example.com/2", 20, new ArrayList<>()));
        when(jobRepositoryMock.findAll()).thenReturn(jobs);

        List<PersistedMonitoringJob> result = jobService.getMonitoringJobs();

        assertNotNull(result);
        assertEquals(jobs.size(), result.size());
        assertEquals(jobs.get(0).getJobName(), result.get(0).getJobName());
        assertEquals(jobs.get(0).getUrl(), result.get(0).getUrl());
        assertEquals(jobs.get(0).getIntervalInSeconds(), result.get(0).getIntervalInSeconds());
        assertNotNull(result.get(0).getCreationDate());
        assertEquals(jobs.get(1).getJobName(), result.get(1).getJobName());
        assertEquals(jobs.get(1).getUrl(), result.get(1).getUrl());
        assertEquals(jobs.get(1).getIntervalInSeconds(), result.get(1).getIntervalInSeconds());
        assertNotNull(result.get(1).getCreationDate());
        verify(jobRepositoryMock).findAll();
    }

}