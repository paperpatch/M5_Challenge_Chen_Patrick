package com.company.gamestoreinvoicing.repository;

import com.company.gamestoreinvoicing.model.ProcessingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessingFeeRepository extends JpaRepository<ProcessingFee, String> {
}
