package com.hanse.codechallenge.utils.conversion;

import com.hanse.codechallenge.controller.dto.JobResultDTO;
import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MonitoringJobToDtoConverter implements Converter<PersistedMonitoringJob, MonitoringJobDTO> {
    @Override
    public MonitoringJobDTO convert(PersistedMonitoringJob source) {
        return new MonitoringJobDTO(
                source.getJobName(),
                source.getUrl(),
                source.getIntervalInSeconds(),
                source.getCreationDate(),
                source.getResults() != null ? source.getResults().stream().map(r -> new JobResultDTO(r.getResult(), r.getResponseTime(), r.getStatusName(), r.getInfo())).collect(Collectors.toList()) : null
                );
    }
}
