package com.hanse.codechallenge.validation;

import com.hanse.codechallenge.controller.dto.JobResultSearchCriteriaDTO;
import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.controller.dto.TimeRange;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import com.hanse.codechallenge.utils.validation.RequestBodyValidation;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestBodyValidationTest {

    @Mock
    MonitoringJobRepository jobRepository;

    private RequestBodyValidation requestBodyValidation;

    @BeforeEach
    void setUp() {
        requestBodyValidation = new RequestBodyValidation(jobRepository);
    }

    @Test
    void validateMonitorJobOnCreate_ThrowsException_WhenJobLimitReached() {
        when(jobRepository.count()).thenReturn(5L);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> requestBodyValidation.validateMonitorJobOnCreate(new MonitoringJobDTO()));
        assertEquals("No more monitoring jobs are allowed!", exception.getMessage());
        verify(jobRepository, times(1)).count();
    }

    @Test
    void validateMonitorJobOnCreate_ThrowsException_WhenJobNameExists() {
        PersistedMonitoringJob persistedMonitoringJob = new PersistedMonitoringJob();
        persistedMonitoringJob.setJobName("test");
        when(jobRepository.count()).thenReturn(3L);
        when(jobRepository.findTopByJobName(anyString())).thenReturn(Optional.of(persistedMonitoringJob));

        MonitoringJobDTO monitoringJobDTO = new MonitoringJobDTO();
        monitoringJobDTO.setJobName("test");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestBodyValidation.validateMonitorJobOnCreate(monitoringJobDTO));
        assertEquals("A monitoring job with that name already exists!", exception.getMessage());
    }

    @Test
    void validateMonitorJobOnCreate_ThrowsException_WhenIntervalIsLessThanTenSeconds() {
        MonitoringJobDTO dto = new MonitoringJobDTO();
        dto.setIntervalInSeconds(5);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestBodyValidation.validateMonitorJobOnCreate(dto));
        assertEquals("The interval specified should be higher than 10 seconds!", exception.getMessage());
    }


    @Test
    void validateMonitorJobOnCreate_ThrowsException_WhenUrlNotSecure() {
        // Arrange
        MonitoringJobDTO dto = new MonitoringJobDTO();
        dto.setUrl("http://example.com");
        dto.setIntervalInSeconds(15);
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestBodyValidation.validateMonitorJobOnCreate(dto));
        assertEquals("Please define the secure url like: https://your_url", exception.getMessage());
    }

    @Test
    void validateMonitorJobOnUpdate_ThrowsException_WhenJobNameDoesNotExist() {
        // Arrange
        when(jobRepository.findTopByJobName(anyString())).thenReturn(Optional.empty());
        MonitoringJobDTO dto = new MonitoringJobDTO();
        dto.setJobName("name");
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestBodyValidation.validateMonitorJobOnUpdate(dto));
        assertEquals("No monitor job with the specified name: name", exception.getMessage());

        // Verify interactions
        verify(jobRepository, times(1)).findTopByJobName(anyString());
    }

    @Test
    void validateMonitorJobOnUpdate_ThrowsException_WhenIntervalIsLessThanTenSeconds() {
        PersistedMonitoringJob persistedMonitoringJob = new PersistedMonitoringJob();
        persistedMonitoringJob.setJobName("name");
        when(jobRepository.findTopByJobName(anyString())).thenReturn(Optional.of(persistedMonitoringJob));

        MonitoringJobDTO dto = new MonitoringJobDTO();
        dto.setIntervalInSeconds(5);
        dto.setJobName("name");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestBodyValidation.validateMonitorJobOnUpdate(dto));
        assertEquals("The interval specified should be higher than 10 seconds!", exception.getMessage());
    }

    @Test
    void validateMonitorJobOnUpdate_ThrowsException_WhenUrlNotSecure() {
        PersistedMonitoringJob persistedMonitoringJob = new PersistedMonitoringJob();
        persistedMonitoringJob.setJobName("name");
        when(jobRepository.findTopByJobName(anyString())).thenReturn(Optional.of(persistedMonitoringJob));
        // Arrange
        MonitoringJobDTO dto = new MonitoringJobDTO();
        dto.setUrl("http://example.com");
        dto.setIntervalInSeconds(15);
        dto.setJobName("name");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestBodyValidation.validateMonitorJobOnUpdate(dto));
        assertEquals("Please define the secure url like: https://your_url", exception.getMessage());
    }

    @Test
    void validateSearchResultCriteria_ThrowsException_WhenLowerBoundIsAfterUpperBound() {
        // Arrange
        JobResultSearchCriteriaDTO dto = new JobResultSearchCriteriaDTO();
        dto.setTimeRange(new TimeRange(Instant.now().plusMillis(150000), Instant.now()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> requestBodyValidation.validateSearchResultCriteria(dto));
        assertEquals("The lower time bound specified in the time range must be before the higher bound!", exception.getMessage());
    }
}

