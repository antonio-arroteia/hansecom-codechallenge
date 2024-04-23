package com.hanse.codechallenge.service;

import com.hanse.codechallenge.enums.Result;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringResult;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class MonitoringJobExecutionService {

    @Autowired
    private MonitoringJobRepository jobRepository;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private Environment environment;

    private final Logger logger = LoggerFactory.getLogger(MonitoringJobExecutionService.class);

    @EventListener(ApplicationReadyEvent.class)
    private void init(){
       initializeTasks();
    }

    public void updateTasks(){
        stopTasks();
        initializeTasks(); //Initialize tasks after shutdown
    }

    public void stopTasks(){
        if (taskScheduler.isRunning()) {
            taskScheduler.shutdown(); // Shutdown the task scheduler
            try {
                taskScheduler.getScheduledExecutor().awaitTermination(5, TimeUnit.SECONDS); // Wait for shutdown
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Error while shutting down task scheduler: " + e.getLocalizedMessage());
            }
        }
    }

    private void initializeTasks(){
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
                result.getResult(), result.getDurationInMillis(), result.getRequestStatus().name(), (result.getRequestStatus() != HttpStatus.OK) ? result.getRequestStatus().getReasonPhrase() :  ""
        );
        monitoringJob.addResult(monitoringResult);
        jobRepository.save(monitoringJob);
    }


    private JobResult testUrl(String jobName, String url) {
        OkHttpClient client = new OkHttpClient();
        try {

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            long startTime = System.currentTimeMillis();
            Response response = client.newCall(request).execute();

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            if(response.isSuccessful()) {
                logger.info("Monitoring Job: "+ jobName + " URL: " + url + " - Success, Response Time: " + responseTime + " ms");
                return new JobResult(Result.SUCCESS, responseTime, HttpStatus.valueOf(response.code()));

            } else {
                logger.info("Monitoring Job: "+ jobName + " URL: " + url + " - Failed, Response Code: " + response.code()," Response Time: " + responseTime +" ms");
                return new JobResult(Result.FAILED, responseTime, HttpStatus.valueOf(response.code()));

            }

        } catch (IOException e) {
            logger.error("Monitoring Job: "+ jobName + " URL: " + url + " - Failed, Exception: Impossible connecting to " + e.getMessage());
            return new JobResult(Result.FAILED,0,HttpStatus.NOT_FOUND);

        } finally {
                client.dispatcher().executorService().shutdown();
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
