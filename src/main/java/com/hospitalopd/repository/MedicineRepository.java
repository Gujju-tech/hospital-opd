package com.hospitalopd.repository;

import com.hospitalopd.model.Medicine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByActiveTrueOrderByNameAsc();
}
