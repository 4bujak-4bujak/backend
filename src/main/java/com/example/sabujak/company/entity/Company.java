package com.example.sabujak.company.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @NotNull
    @Column(name = "company_name")
    private String companyName;

    @NotNull
    @Column(name = "company_email_domain")
    private String companyEmailDomain;

    @NotNull
    @Column(name = "business_type")
    private String businessType;

    @Positive
    @Column(name = "company_head_count")
    private int companyHeadCount;

    @NotNull
    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;

    @NotNull
    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @NotNull
    @Column(name = "representative_name")
    private String representativeName;

    @NotNull
    @Column(name = "representative_contact")
    private String representativeContact;

    @NotNull
    @Column(name = "contract_manager_name")
    private String contractManagerName;

    @NotNull
    @Column(name = "contract_manager_contact")
    private String contractManagerContact;
}
