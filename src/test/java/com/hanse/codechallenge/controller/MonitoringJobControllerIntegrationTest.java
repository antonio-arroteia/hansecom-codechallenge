package com.hanse.codechallenge.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MonitoringJobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MonitoringJobRepository monitoringJobRepository;


    private ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    @AfterEach
    public void setup(){
        monitoringJobRepository.deleteAllInBatch();
        monitoringJobRepository.flush();
    }


    @Test
    public void testConfigureNewMonitorJob() throws Exception {
        MonitoringJobDTO inputDTO = new MonitoringJobDTO("JobName", "https://example.com", 60, Instant.now(), null);
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/jobs/configure-new-monitoring-job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jobName", Matchers.is(inputDTO.getJobName())))
                .andExpect(jsonPath("$.url", Matchers.is(inputDTO.getUrl())));


    }

    @Test
    public void testUpdateMonitorJob() throws Exception {
        MonitoringJobDTO inputDTO = new MonitoringJobDTO("JobName", "https://example-test.com", 50L, Instant.now(),null);
        PersistedMonitoringJob persistedJob = new PersistedMonitoringJob(1L, "JobName", "https://example.com", 60L, Collections.emptyList());
        objectMapper.registerModule(new JavaTimeModule());

        monitoringJobRepository.saveAndFlush(persistedJob);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/jobs/update-monitoring-job")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jobName", Matchers.is(persistedJob.getJobName())))
                .andExpect(jsonPath("$.url", Matchers.is(inputDTO.getUrl())))
                .andExpect(jsonPath("$.intervalInSeconds", Matchers.is((int)inputDTO.getIntervalInSeconds())));


    }

    @Test
    public void testGetAllMonitoringJobs() throws Exception {

        List<PersistedMonitoringJob> jobsList = List.of(
            new PersistedMonitoringJob(null, "JobName1", "https://example.com", 60L, Collections.emptyList()),
            new PersistedMonitoringJob(null, "JobName2", "https://example.com", 60L, Collections.emptyList()),
            new PersistedMonitoringJob(null, "JobName3", "https://example.com", 60L, Collections.emptyList())
        );

        monitoringJobRepository.saveAllAndFlush(jobsList);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/jobs/get-all-monitoring-jobs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].jobName", Matchers.is("JobName1")))
                .andExpect(jsonPath("$[1].jobName", Matchers.is("JobName2")))
                .andExpect(jsonPath("$[2].jobName", Matchers.is("JobName3")));


    }
}