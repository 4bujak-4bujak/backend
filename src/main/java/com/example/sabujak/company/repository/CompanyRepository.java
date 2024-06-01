package com.example.sabujak.company.repository;

import com.example.sabujak.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByCompanyEmailDomain(String emailDomain);

    Optional<Company> findByCompanyEmailDomain(String emailDomain);
}
