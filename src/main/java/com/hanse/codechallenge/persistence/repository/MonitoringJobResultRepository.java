package com.hanse.codechallenge.persistence.repository;

import com.hanse.codechallenge.controller.dto.JobResultSearchCriteriaDTO;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringJob;
import com.hanse.codechallenge.persistence.entity.PersistedMonitoringResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MonitoringJobResultRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<PersistedMonitoringResult> searchAllBy(JobResultSearchCriteriaDTO criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PersistedMonitoringResult> query = cb.createQuery(PersistedMonitoringResult.class);
        Root<PersistedMonitoringJob> monitoringJobRoot = query.from(PersistedMonitoringJob.class);
        Join<PersistedMonitoringJob, PersistedMonitoringResult> resultJoin = monitoringJobRoot.join("results");

        List<Predicate> predicates = new ArrayList<>();

        // Add conditions based on criteria
        if (criteria.getJobName() != null) {
            predicates.add(cb.equal(monitoringJobRoot.get("jobName"), criteria.getJobName()));
        }
        if(criteria.getResult() != null){
            predicates.add(cb.equal(resultJoin.get("result"), criteria.getResult()));

        }
        if(criteria.getTimeRange() != null){
            Instant minRange = criteria.getTimeRange().getLowerBound();
            Instant maxRange = criteria.getTimeRange().getUpperBound();
            if(minRange !=null && maxRange != null ) {
                predicates.add(cb.greaterThanOrEqualTo(resultJoin.get("creationDate"), minRange));
                predicates.add(cb.lessThanOrEqualTo(resultJoin.get("creationDate"), maxRange));
            } else {
                if(minRange != null) {
                    predicates.add(cb.greaterThanOrEqualTo(resultJoin.get("creationDate"), minRange));
                }
                if(maxRange != null) {
                    predicates.add(cb.lessThanOrEqualTo(resultJoin.get("creationDate"), maxRange));
                }
            }
        }

        if(criteria.getStatus() != null){
            predicates.add(cb.equal(resultJoin.get("statusName"), criteria.getStatus().name()));
        }


        query.select(resultJoin).where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

}
