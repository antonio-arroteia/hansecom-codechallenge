package com.hanse.codechallenge.service;

import com.hanse.codechallenge.enums.Result;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringResult;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@Service
public class MonitoringJobExecutionService {

    @Autowired
    private MonitoringJobRepository jobRepository;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private Environment environment;

    private final Logger logger = LoggerFactory.getLogger(MonitoringJobExecutionService.class);

    public void updateTasks(){
        if(taskScheduler.isRunning())taskScheduler.getScheduledExecutor().shutdown();
        initializeTasks();
    }


    @EventListener(ApplicationReadyEvent.class)
    public void initializeTasks(){
        if(!taskScheduler.isRunning()) taskScheduler.initialize();
        if (!environment.matchesProfiles("test")) {
            jobRepository.findAll().parallelStream().forEach(job -> {
                Runnable task = () -> executeMonitoringJobs(job);
                taskScheduler.scheduleWithFixedDelay(task, Duration.ofSeconds(job.getIntervalInSeconds()));
            });
        }
    }

    protected void executeMonitoringJobs(PersistedMonitoringJob monitoringJob) {
        JobResult result = testUrl(monitoringJob.getJobName(), monitoringJob.getUrl());
        addJobResult(monitoringJob, result);

    }

    private void addJobResult(PersistedMonitoringJob monitoringJob, JobResult result){
        PersistedMonitoringResult monitoringResult = new PersistedMonitoringResult(
                result.result, result.durationInMillis, result.requestStatus.name(), (result.requestStatus != HttpStatus.OK) ? result.requestStatus.getReasonPhrase() :  ""
        );
        monitoringJob.addResult(monitoringResult);
        jobRepository.save(monitoringJob);
    }


    private JobResult testUrl(String jobName, String url) {
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");

            long startTime = System.currentTimeMillis();
            connection.connect();

            long endTime = System.currentTimeMillis();
            int responseCode = connection.getResponseCode();
            long responseTime = endTime - startTime;

            if(responseCode == HttpURLConnection.HTTP_OK) {
                logger.info("Monitoring Job: "+ jobName + "URL: " + url + " - Success, Response Time: " + responseTime + " ms");
                return new JobResult(Result.SUCCESS, responseTime, HttpStatus.OK);

            } else {
                logger.info("Monitoring Job: "+ jobName + "URL: " + url + " - Failed, Response Code: " + responseCode);
                return new JobResult(Result.FAILED, responseTime, HttpStatus.valueOf(responseCode));

            }

        } catch (IOException e) {
            logger.error("Monitoring Job: "+ jobName + "URL: " + url + " - Failed, Exception: Impossible connecting to " + e.getMessage());
            return new JobResult(Result.FAILED,0,HttpStatus.NOT_FOUND);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Data
    private class JobResult{
        private Result result;
        private long durationInMillis;
        private HttpStatus requestStatus;
        private Instant timestamp = Instant.now();

        public JobResult(Result result, long durationInMillis, HttpStatus requestStatus) {
            this.result = result;
            this.durationInMillis = durationInMillis;
            this.requestStatus = requestStatus;
        }
    }

}
