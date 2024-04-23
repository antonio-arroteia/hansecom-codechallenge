package com.hanse.codechallenge.utils.validation;

import com.hanse.codechallenge.controller.dto.JobResultSearchCriteriaDTO;
import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.repository.MonitoringJobRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RequestBodyValidation {

    public RequestBodyValidation(MonitoringJobRepository jobRepository) {
        RequestBodyValidation.jobRepository = jobRepository;
    }

    private static MonitoringJobRepository jobRepository;

    private static final String urlRegex ="https://";


    public static void validateMonitorJobOnCreate(MonitoringJobDTO monitoringJobDTO){

        if(jobRepository.count() == 5 ) throw new RuntimeException("No more monitoring jobs are allowed!");
        if(jobRepository.findTopByJobName(monitoringJobDTO.getJobName()).isPresent()) throw new IllegalArgumentException("A monitoring job with that name already exists!");
        if(monitoringJobDTO.getIntervalInSeconds() < 10) throw new IllegalArgumentException("The interval specified should be higher than 10 seconds!"); //to regulate a bit the amount of traffic
        if(!monitoringJobDTO.getUrl().startsWith(urlRegex)) throw  new IllegalArgumentException("Please define the secure url like: https://your_url");
    }

    public static void validateMonitorJobOnUpdate(MonitoringJobDTO monitoringJobDTO) {

        Optional<PersistedMonitoringJob> monitorJobToUpdate = jobRepository.findTopByJobName(monitoringJobDTO.getJobName());
        if (monitorJobToUpdate.isEmpty()) throw new IllegalArgumentException("No monitor job with the specified name: " + monitoringJobDTO.getJobName()) ;
        if(monitoringJobDTO.getIntervalInSeconds() < 10) throw new IllegalArgumentException("The interval specified should be higher than 10 seconds!"); //to regulate a bit the amount of traffic
        if(monitoringJobDTO.getUrl() != null && !monitoringJobDTO.getUrl().startsWith(urlRegex)) throw  new IllegalArgumentException("Please define the secure url like: https://your_url");
    }


    public static void validateSearchResultCriteria(JobResultSearchCriteriaDTO searchCriteriaDTO){
        if(searchCriteriaDTO != null && searchCriteriaDTO.getTimeRange() != null && searchCriteriaDTO.getTimeRange().getLowerBound() != null && searchCriteriaDTO.getTimeRange().getUpperBound() != null ){
            if(searchCriteriaDTO.getTimeRange().getLowerBound().isAfter(searchCriteriaDTO.getTimeRange().getUpperBound())){
                throw new IllegalArgumentException("The lower time bound specified in the time range must be before the higher bound!");
            }
        }
    }

    public static void setJobRepository(MonitoringJobRepository jobRepository) {
        RequestBodyValidation.jobRepository = jobRepository;
    }

}
