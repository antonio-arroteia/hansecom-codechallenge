package com.hanse.codechallenge.service;


import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
public class MonitoringJobExecutionTest {
    @Mock
    private MonitoringJobRepository jobRepositoryMock;

    @Mock
    private ThreadPoolTaskScheduler taskSchedulerMock;

    @Mock
    private ScheduledExecutorService executorServiceMock;

    @Mock
    private Environment environmentMock;

    @InjectMocks
    private MonitoringJobExecutionService executionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testUpdateTasks() throws InterruptedException {
        Mockito.when(taskSchedulerMock.isRunning()).thenReturn(true);
        Mockito.when(taskSchedulerMock.getScheduledExecutor()).thenReturn(executorServiceMock);

        executionService.updateTasks();

        Mockito.verify(taskSchedulerMock, Mockito.times(2)).isRunning();
        Mockito.verify(taskSchedulerMock, Mockito.times(1)).getScheduledExecutor();
        Mockito.verify(taskSchedulerMock.getScheduledExecutor(), Mockito.times(1)).awaitTermination(5L, TimeUnit.SECONDS);

    }

}
