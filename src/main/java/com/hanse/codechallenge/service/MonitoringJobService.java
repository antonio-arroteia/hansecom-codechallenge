package com.hanse.codechallenge.service;

import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringResult;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import com.hanse.codechallenge.utils.validation.RequestBodyValidation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        monitorJobToUpdate.setJobName(monitoringJobDTO.getJobName());
        if(monitoringJobDTO.getUrl() != null) monitorJobToUpdate.setUrl(monitoringJobDTO.getUrl());
        monitorJobToUpdate.setIntervalInSeconds(monitoringJobDTO.getIntervalInSeconds());

        if(updateTasks) monitoringJobExecutionService.updateTasks();
        return jobRepository.save(monitorJobToUpdate);

    }

    public List<PersistedMonitoringJob> getMonitoringJobs(){
        return jobRepository.findAll();
    }

    @Transactional
    public boolean deleteMonitorJobByName(String name) {
        Optional<PersistedMonitoringJob> jobToDelete = jobRepository.findTopByJobName(name);
        if (jobToDelete.isEmpty()) throw new EntityNotFoundException("No monitor job with the specified name: " + name);
        monitoringJobExecutionService.stopTasks();
        try {
            if (!jobToDelete.get().getResults().isEmpty()) {
                jobToDelete.get().setResults(new ArrayList<>());
                jobRepository.save(jobToDelete.get());
            }
            jobRepository.delete(jobToDelete.get());
            monitoringJobExecutionService.updateTasks();
            return  true;
        }catch(Exception e){
            monitoringJobExecutionService.updateTasks();
            return false;
        }
    }

}
