package com.hanse.codechallenge.utils.conversion;

import com.hanse.codechallenge.controller.dto.JobResultDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringResult;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MonitoringJobResultToDtoConverter implements Converter<PersistedMonitoringResult, JobResultDTO> {

    @Override
    public JobResultDTO convert(PersistedMonitoringResult source) {
        return new JobResultDTO(
                source.getResult(),
                source.getResponseTime(),
                source.getStatusName(),
                source.getInfo()
        );
    }
}