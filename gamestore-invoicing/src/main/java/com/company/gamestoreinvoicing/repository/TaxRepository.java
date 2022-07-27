package com.company.gamestoreinvoicing.repository;

import com.company.gamestoreinvoicing.model.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends JpaRepository<Tax, String> {
}
