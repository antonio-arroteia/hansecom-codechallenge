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
    public void testUpdateTasks() {
        Mockito.when(taskSchedulerMock.isRunning()).thenReturn(true);
        Mockito.when(taskSchedulerMock.getScheduledExecutor()).thenReturn(executorServiceMock);

        executionService.updateTasks();

        Mockito.verify(taskSchedulerMock, Mockito.times(2)).isRunning();
        Mockito.verify(taskSchedulerMock, Mockito.times(1)).getScheduledExecutor();
        Mockito.verify(taskSchedulerMock.getScheduledExecutor(), Mockito.times(1)).shutdown();
    }

    @Test
    public void testInitializeTasks_WhenNotRunningAndNotTestProfile_ShouldInitializeAndScheduleTasks() {
        Mockito.when(taskSchedulerMock.isRunning()).thenReturn(false);
        Mockito.when(environmentMock.matchesProfiles("test")).thenReturn(false);
        Mockito.when(jobRepositoryMock.findAll()).thenReturn(List.of(new PersistedMonitoringJob()));


        executionService.initializeTasks();

        Mockito.verify(taskSchedulerMock, Mockito.times(1)).isRunning();
        Mockito.verify(taskSchedulerMock, Mockito.times(1)).initialize();
        Mockito.verify(jobRepositoryMock, Mockito.times(1)).findAll();
        Mockito.verify(taskSchedulerMock, Mockito.times(1)).scheduleWithFixedDelay(ArgumentMatchers.any(Runnable.class), Mockito.any());
    }

}
