package com.hanse.codechallenge.controller;

import com.hanse.codechallenge.controller.dto.JobResultDTO;
import com.hanse.codechallenge.controller.dto.JobResultSearchCriteriaDTO;
import com.hanse.codechallenge.service.MonitoringJobResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/job-results")
@Tag(name = "Controller for searching monitoring job results")
public class MonitoringJobResultsController {

    @Autowired
    private MonitoringJobResultService monitoringJobResultService;

    @Autowired
    private ConversionService conversionService;


    @Operation(summary = "Search for job results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the monitoring job",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    @PostMapping("/search")
    public List<JobResultDTO> searchJobResults(@RequestBody JobResultSearchCriteriaDTO searchCriteria){
        return monitoringJobResultService.searchMonitoringJobResults(searchCriteria).stream().map(result ->
                conversionService.convert(result, JobResultDTO.class)).collect(Collectors.toList());
    }

}
