package com.hanse.codechallenge.controller;

import com.hanse.codechallenge.controller.dto.MonitoringJobDTO;
import com.hanse.codechallenge.service.MonitoringJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@Tag(name = "Controller for configuring monitor jobs")
@RestController
@RequestMapping("/api/jobs")
public class MonitoringJobController {

    @Autowired
    private MonitoringJobService jobService;

    @Autowired
    private ConversionService conversionService;


    @Operation(summary = "Configure a new monitoring job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully configured a new monitoring job"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
    })
    @PostMapping(value = "/configure-new-monitoring-job", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public MonitoringJobDTO configureNewMonitorJob(@Valid @RequestBody MonitoringJobDTO monitoringJobDTO) {
        return conversionService.convert(jobService.createMonitorJob(monitoringJobDTO), MonitoringJobDTO.class);
    }


    @Operation(summary = "Update an existing monitoring job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the monitoring job",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Monitoring job not found")
    })
    @PutMapping(value = "/update-monitoring-job", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public MonitoringJobDTO updateMonitorJob(@Valid @RequestBody MonitoringJobDTO monitoringJobDTO){
        return conversionService.convert(jobService.updateMonitoringJob(monitoringJobDTO), MonitoringJobDTO.class);
    }

    @Operation(summary = "Get all monitoring jobs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of monitoring jobs",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @GetMapping(value = "/get-all-monitoring-jobs", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<MonitoringJobDTO> getAllMonitoringJobs(){
        return jobService.getMonitoringJobs().stream().map(job ->
                conversionService.convert(job, MonitoringJobDTO.class)).collect(Collectors.toList());
    }

    @Operation(summary = "Delete a monitoring job by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of monitoring jobs",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    @DeleteMapping(value = "/delete-by-name/{jobName}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity deleteByJobName(@Schema(description = "Job name to delete") @PathVariable String jobName){
        return jobService.deleteMonitorJobByName(jobName) ? ResponseEntity.ok().build() : ResponseEntity.status(409).build();
    }

}
