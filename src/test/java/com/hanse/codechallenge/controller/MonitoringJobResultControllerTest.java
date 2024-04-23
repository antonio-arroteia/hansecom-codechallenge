package com.hanse.codechallenge.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanse.codechallenge.controller.dto.JobResultDTO;
import com.hanse.codechallenge.controller.dto.JobResultSearchCriteriaDTO;
import com.hanse.codechallenge.controller.dto.TimeRange;
import com.hanse.codechallenge.enums.Result;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringResult;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import com.hanse.codechallenge.persistence.repository.MonitoringJobResultRepository;
import com.hanse.codechallenge.service.MonitoringJobResultService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MonitoringJobResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MonitoringJobRepository jobRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        jobRepository.deleteAllInBatch();
        jobRepository.flush();
    }
    @Test
    public void testSearchJobResults() throws Exception {

        List<PersistedMonitoringJob> jobList = List.of(
                new PersistedMonitoringJob(null, "test1", "https://example1.com", 10, List.of(
                        new PersistedMonitoringResult(Result.SUCCESS, 200, "OK", ""),
                        new PersistedMonitoringResult(Result.FAILED, 10, "NOT FOUND", ""),
                        new PersistedMonitoringResult(Result.SUCCESS, 300, "OK", "")
                )),
                new PersistedMonitoringJob(null, "test2", "https://example2.com", 15, List.of(
                        new PersistedMonitoringResult(Result.FAILED, 20, "NOT FOUND", ""),
                        new PersistedMonitoringResult(Result.FAILED, 10, "NOT FOUND", ""),
                        new PersistedMonitoringResult(Result.SUCCESS, 300, "OK", ""))
                ),
                new PersistedMonitoringJob(null, "test3", "https://example3.com", 10, Collections.emptyList()),
                new PersistedMonitoringJob(null, "test4", "https://example4.com", 20, List.of(
                        new PersistedMonitoringResult(Result.SUCCESS, 200, "OK", ""),
                        new PersistedMonitoringResult(Result.SUCCESS, 150, "OK", ""),
                        new PersistedMonitoringResult(Result.SUCCESS, 300, "OK", ""))
                )

        );

        jobRepository.saveAllAndFlush(jobList);

        List<PersistedMonitoringResult> resultList = jobList.stream()
                .flatMap(job -> job.getResults().stream()).filter(result -> result.getResult() == Result.SUCCESS && result.getStatusName().equals("OK"))
                .collect(Collectors.toList());

        JobResultSearchCriteriaDTO searchCriteria = new JobResultSearchCriteriaDTO();
        searchCriteria.setResult(Result.SUCCESS);
        searchCriteria.setStatus(HttpStatus.OK);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/job-results/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchCriteria)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.*", IsCollectionWithSize.hasSize(resultList.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].result", CoreMatchers.equalTo(resultList.get(0).getResult().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].statusName", CoreMatchers.equalTo(resultList.get(0).getStatusName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].result", CoreMatchers.equalTo(resultList.get(1).getResult().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].statusName", CoreMatchers.equalTo(resultList.get(1).getStatusName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].result", CoreMatchers.equalTo(resultList.get(2).getResult().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].statusName", CoreMatchers.equalTo(resultList.get(2).getStatusName())));
                    //...
    }
}
