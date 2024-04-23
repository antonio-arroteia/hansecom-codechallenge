package com.hanse.codechallenge.utils.conversion;

import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DtoToMonitoringJobConverter implements Converter<MonitoringJobDTO, PersistedMonitoringJob> {


    @Override
    public PersistedMonitoringJob convert(MonitoringJobDTO source) {
        return new PersistedMonitoringJob(
                null,
                source.getJobName(),
                source.getUrl(),
                source.getIntervalInSeconds(),
                null
        );
    }

}

