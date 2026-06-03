package com.hospitalopd.repository;

import com.hospitalopd.model.OpdVisit;
import com.hospitalopd.model.VisitStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpdVisitRepository extends JpaRepository<OpdVisit, Long> {
    List<OpdVisit> findAllByOrderByVisitDateDesc();

    List<OpdVisit> findByStatusOrderByVisitDateDesc(VisitStatus status);

    long countByStatus(VisitStatus status);
}
