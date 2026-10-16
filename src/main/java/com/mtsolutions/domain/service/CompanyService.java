package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.ContextComponent;
import com.mtsolutions.application.component.ContextUser;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AccessDeniedException;
import com.mtsolutions.application.exception.CompanyAlreadyExistsException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.exception.UserNotFoundException;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.constant.UserRole;
import com.mtsolutions.domain.dto.request.CreateCompanyRequestDto;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.entity.CompanyMember;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.repository.CompanyMemberRepository;
import com.mtsolutions.domain.repository.CompanyRepository;
import com.mtsolutions.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@ApplicationScoped
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final UserRepository userRepository;
    private final ContextComponent contextComponent;
    private final DateComponent dateComponent;

    public CompanyService(CompanyRepository companyRepository,
                          CompanyMemberRepository companyMemberRepository,
                          UserRepository userRepository,
                          ContextComponent contextComponent,
                          DateComponent dateComponent) {
        this.companyRepository = companyRepository;
        this.companyMemberRepository = companyMemberRepository;
        this.userRepository = userRepository;
        this.contextComponent = contextComponent;
        this.dateComponent = dateComponent;
    }

    @Transactional
    public Company createCompany(CreateCompanyRequestDto request) throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException(Features.CREATE_COMPANY);
        }

        log.info("Creating company {} for user: {}", request.name(), currentUser.getUserId());

        if (this.companyRepository.existsByCnpj(request.cnpj())) {
            throw new CompanyAlreadyExistsException(Features.CREATE_COMPANY);
        }

        User user = this.userRepository.findByIdOptional(currentUser.getUserId())
                .orElseThrow(() -> new UserNotFoundException(Features.CREATE_COMPANY));

        LocalDateTime now = this.dateComponent.now();
        Company company = Company.builder()
                .name(request.name())
                .cnpj(request.cnpj())
                .plan(request.plan())
                .status(CompanyStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        this.companyRepository.persist(company);

        CompanyMember member = CompanyMember.builder()
                .user(user)
                .company(company)
                .role(UserRole.ADMIN)
                .build();

        this.companyMemberRepository.persist(member);

        log.info("Company created with ID: {}", company.getCompanyId());
        return company;
    }
}
