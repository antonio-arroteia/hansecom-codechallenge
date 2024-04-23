package com.hanse.codechallenge.service;


import com.hanse.codechallenge.controller.dto.JobResultSearchCriteriaDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringResult;
import com.hanse.codechallenge.persistence.repository.MonitoringJobResultRepository;
import com.hanse.codechallenge.utils.validation.RequestBodyValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitoringJobResultService {

    @Autowired
    private MonitoringJobResultRepository monitoringJobResultRepository;

    public List<PersistedMonitoringResult> searchMonitoringJobResults(JobResultSearchCriteriaDTO searchCriteria ){
        RequestBodyValidation.validateSearchResultCriteria(searchCriteria);
        return monitoringJobResultRepository.searchAllBy(searchCriteria);

    }
}
