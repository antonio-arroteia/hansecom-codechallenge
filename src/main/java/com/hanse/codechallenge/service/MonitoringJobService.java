package com.hanse.codechallenge.service;

import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import com.hanse.codechallenge.utils.validation.RequestBodyValidation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
public class MonitoringJobService {

    @Autowired
    private MonitoringJobExecutionService monitoringJobExecutionService;

    @Autowired
    private MonitoringJobRepository jobRepository;

    @Autowired
    private ConversionService conversionService;

    public MonitoringJobService(MonitoringJobExecutionService monitoringJobExecutionService, MonitoringJobRepository jobRepository, ConversionService conversionService) {
        this.monitoringJobExecutionService = monitoringJobExecutionService;
        this.jobRepository = jobRepository;
        this.conversionService = conversionService;
    }

    @Transactional
    public PersistedMonitoringJob createMonitorJob(MonitoringJobDTO monitoringJobDTO){

        RequestBodyValidation.validateMonitorJobOnCreate(monitoringJobDTO);
        PersistedMonitoringJob monitorJobToSave = jobRepository.save(Objects.requireNonNull(conversionService.convert(monitoringJobDTO, PersistedMonitoringJob.class),"Monitor Job must not be null"));
        monitoringJobExecutionService.updateTasks();
        return monitorJobToSave;
    }


    @Transactional
    public PersistedMonitoringJob updateMonitoringJob(MonitoringJobDTO monitoringJobDTO){

        RequestBodyValidation.validateMonitorJobOnUpdate(monitoringJobDTO);
        PersistedMonitoringJob monitorJobToUpdate =jobRepository.findTopByJobName(monitoringJobDTO.getJobName()).get();

        boolean updateTasks = monitorJobToUpdate.getIntervalInSeconds() != monitorJobToUpdate.getIntervalInSeconds() ||
                !monitorJobToUpdate.getJobName().equals(monitoringJobDTO.getJobName()) || !monitorJobToUpdate.getUrl().equals(monitoringJobDTO.getUrl());

        monitorJobToUpdate.setUrl(monitoringJobDTO.getUrl());
        monitorJobToUpdate.setJobName(monitoringJobDTO.getJobName());
        monitorJobToUpdate.setIntervalInSeconds(monitoringJobDTO.getIntervalInSeconds());

        if(updateTasks) monitoringJobExecutionService.updateTasks();
        return jobRepository.save(monitorJobToUpdate);

    }

    public List<PersistedMonitoringJob> getMonitoringJobs(){
        return jobRepository.findAll();
    }

}
