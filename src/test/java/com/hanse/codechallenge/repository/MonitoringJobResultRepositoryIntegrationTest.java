package com.hanse.codechallenge.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import com.hanse.codechallenge.controller.dto.TimeRange;
import com.hanse.codechallenge.enums.Result;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import com.hanse.codechallenge.persistence.repository.MonitoringJobResultRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.hanse.codechallenge.controller.dto.JobResultSearchCriteriaDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringResult;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MonitoringJobResultRepositoryIntegrationTest {

    @Autowired
    private MonitoringJobRepository monitoringJobRepository;

    @Autowired
    private MonitoringJobResultRepository repository;

    @BeforeEach
    void setUp() {
        monitoringJobRepository.deleteAll();
        monitoringJobRepository.flush();
        PersistedMonitoringJob job1 = new PersistedMonitoringJob();
        job1.setJobName("job1");
        job1.setIntervalInSeconds( 10);
        job1.setUrl("http://example.com");

        PersistedMonitoringResult result1 = new PersistedMonitoringResult(Result.SUCCESS, 100, HttpStatus.OK.name(), "Info for result 1");
        PersistedMonitoringResult result2 = new PersistedMonitoringResult(Result.FAILED, 200, HttpStatus.INTERNAL_SERVER_ERROR.name(), "Info for result 2");

        job1.setResults(Arrays.asList(result1, result2));

        PersistedMonitoringJob job2 = new PersistedMonitoringJob();
        job2.setJobName("job2");
        job2.setIntervalInSeconds(10);
        job2.setUrl("http://example.com");

        PersistedMonitoringResult result3 = new PersistedMonitoringResult(Result.SUCCESS, 150, HttpStatus.OK.name(), "Info for result 3");

        job2.setResults(Arrays.asList(result3));

        monitoringJobRepository.saveAll(List.of(job1,job2));
        monitoringJobRepository.flush();
    }

    @AfterEach
    void terminate() {
        monitoringJobRepository.deleteAll();
        monitoringJobRepository.flush();
    }

    @Test
    void testSearchByJobName() {
        JobResultSearchCriteriaDTO criteria = new JobResultSearchCriteriaDTO();
        criteria.setJobName("job1");

        List<PersistedMonitoringResult> results = repository.searchAllBy(criteria);

        assertEquals(2, results.size());
        assertTrue(results.stream().filter(it -> it.getResult() == Result.SUCCESS).count() == 1);
        assertTrue(results.stream().filter(it -> it.getResult() == Result.FAILED).count() == 1);

    }

    @Test
    void testSearchByStatus() {
        JobResultSearchCriteriaDTO criteria = new JobResultSearchCriteriaDTO();
        criteria.setStatus(HttpStatus.OK);

        List<PersistedMonitoringResult> results = repository.searchAllBy(criteria);

        assertEquals(2, results.size());
    }

    @Test
    void testSearchByTimeRange() {
        Instant startTime = Instant.now().minusSeconds(30);
        Instant endTime = Instant.now().plusSeconds(30);

        JobResultSearchCriteriaDTO criteria = new JobResultSearchCriteriaDTO();
        criteria.setTimeRange(new TimeRange(startTime, endTime));

        List<PersistedMonitoringResult> results = repository.searchAllBy(criteria);

        assertEquals(3, results.size());
    }

    @Test
    void testSearchByJobNameAndStatus() {
        JobResultSearchCriteriaDTO criteria = new JobResultSearchCriteriaDTO();
        criteria.setStatus(HttpStatus.OK);

        List<PersistedMonitoringResult> results = repository.searchAllBy(criteria);

        assertEquals(2, results.size());
        assertEquals(Result.SUCCESS, results.get(0).getResult());
        assertEquals(Result.SUCCESS, results.get(1).getResult());

    }

    @Test
    void testSearchByJobNameAndTimeRange() {
        Instant startTime = Instant.now().minusSeconds(20); // Four seconds ago

        JobResultSearchCriteriaDTO criteria = new JobResultSearchCriteriaDTO();
        criteria.setJobName("job2");
        criteria.setTimeRange(new TimeRange(startTime, null));

        List<PersistedMonitoringResult> results = repository.searchAllBy(criteria);

        assertEquals(1, results.size());
        assertEquals(Result.SUCCESS, results.get(0).getResult());
    }

    @Test
    void testSearchByStatusAndTimeRange() {
        Instant endTime = Instant.now().plusSeconds(20); // One second ago

        JobResultSearchCriteriaDTO criteria = new JobResultSearchCriteriaDTO();
        criteria.setStatus(HttpStatus.OK);
        criteria.setTimeRange(new TimeRange(null, endTime));

        List<PersistedMonitoringResult> results = repository.searchAllBy(criteria);

        assertEquals(2, results.size());
        assertEquals(Result.SUCCESS, results.get(0).getResult());
    }

    @Test
    void testSearchByJobNameStatusAndTimeRange() {
        Instant startTime = Instant.now().minusSeconds(20); // Eight seconds ago
        Instant endTime = Instant.now().plusSeconds(20); // One second ago

        JobResultSearchCriteriaDTO criteria = new JobResultSearchCriteriaDTO();
        criteria.setJobName("job2");
        criteria.setStatus(HttpStatus.OK);
        criteria.setTimeRange(new TimeRange(startTime, endTime));

        List<PersistedMonitoringResult> results = repository.searchAllBy(criteria);

        assertEquals(1, results.size());
        assertEquals(Result.SUCCESS, results.get(0).getResult());
    }

    @Test
    void testSearchWithoutCriteria() {
        List<PersistedMonitoringResult> results = repository.searchAllBy(new JobResultSearchCriteriaDTO());

        assertEquals(3, results.size());
    }
}

