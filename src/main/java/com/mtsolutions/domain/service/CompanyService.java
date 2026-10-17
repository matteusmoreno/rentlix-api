package com.mtsolutions.domain.service;

import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.component.ContextComponent;
import com.mtsolutions.application.component.ContextUser;
import com.mtsolutions.application.component.DateComponent;
import com.mtsolutions.application.exception.AccessDeniedException;
import com.mtsolutions.application.exception.CompanyAlreadyExistsException;
import com.mtsolutions.application.exception.CompanyMemberAlreadyExistsException;
import com.mtsolutions.application.exception.CompanyNotFoundException;
import com.mtsolutions.application.exception.CompanyStatusConflictException;
import com.mtsolutions.application.exception.InvalidMemberRoleException;
import com.mtsolutions.application.exception.PictureNotFoundException;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.exception.UserNotFoundException;
import com.mtsolutions.domain.constant.CloudinaryFolder;
import com.mtsolutions.domain.constant.CompanyStatus;
import com.mtsolutions.domain.constant.UserRole;
import com.mtsolutions.domain.dto.request.AddCompanyMemberRequestDto;
import com.mtsolutions.domain.dto.request.CreateCompanyRequestDto;
import com.mtsolutions.domain.entity.Asset;
import com.mtsolutions.domain.entity.Company;
import com.mtsolutions.domain.entity.CompanyMember;
import com.mtsolutions.domain.entity.User;
import com.mtsolutions.domain.model.AssetPage;
import com.mtsolutions.domain.model.PublicCompany;
import com.mtsolutions.domain.repository.AssetRepository;
import com.mtsolutions.domain.repository.CompanyMemberRepository;
import com.mtsolutions.domain.repository.CompanyRepository;
import com.mtsolutions.domain.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@ApplicationScoped
@Slf4j
public class CompanyService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final CompanyRepository companyRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final CloudinaryService cloudinaryService;
    private final ContextComponent contextComponent;
    private final DateComponent dateComponent;

    public CompanyService(CompanyRepository companyRepository,
                          CompanyMemberRepository companyMemberRepository,
                          UserRepository userRepository,
                          AssetRepository assetRepository,
                          CloudinaryService cloudinaryService,
                          ContextComponent contextComponent,
                          DateComponent dateComponent) {
        this.companyRepository = companyRepository;
        this.companyMemberRepository = companyMemberRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.cloudinaryService = cloudinaryService;
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
                .slug(this.slug(request.name()))
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

    public List<CompanyMember> listCompanies() throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException(Features.LIST_COMPANIES);
        }

        log.info("Listing companies for user: {}", currentUser.getUserId());
        return this.companyMemberRepository.findByUserId(currentUser.getUserId());
    }

    @Transactional
    public CompanyMember addMember(UUID companyId, AddCompanyMemberRequestDto request) throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException(Features.ADD_COMPANY_MEMBER);
        }

        Company company = this.companyRepository.findByIdOptional(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(Features.ADD_COMPANY_MEMBER));

        CompanyMember currentMember = this.companyMemberRepository.findByUserIdAndCompanyId(currentUser.getUserId(), companyId)
                .orElseThrow(() -> new AccessDeniedException(Features.ADD_COMPANY_MEMBER));

        if (currentMember.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException(Features.ADD_COMPANY_MEMBER);
        }

        if (request.role() == UserRole.ADMIN) {
            throw new InvalidMemberRoleException(Features.ADD_COMPANY_MEMBER);
        }

        User user = this.userRepository.findByIdOptional(request.userId())
                .orElseThrow(() -> new UserNotFoundException(Features.ADD_COMPANY_MEMBER));

        if (this.companyMemberRepository.existsByUserIdAndCompanyId(user.getUserId(), companyId)) {
            throw new CompanyMemberAlreadyExistsException(Features.ADD_COMPANY_MEMBER);
        }

        CompanyMember member = CompanyMember.builder()
                .user(user)
                .company(company)
                .role(request.role())
                .build();

        this.companyMemberRepository.persist(member);

        log.info("User {} added to company {}", user.getUserId(), company.getCompanyId());
        return member;
    }

    public List<CompanyMember> listMembers(UUID companyId) throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException(Features.LIST_COMPANY_MEMBERS);
        }

        if (this.companyRepository.findByIdOptional(companyId).isEmpty()) {
            throw new CompanyNotFoundException(Features.LIST_COMPANY_MEMBERS);
        }

        if (!this.companyMemberRepository.existsByUserIdAndCompanyId(currentUser.getUserId(), companyId)) {
            throw new AccessDeniedException(Features.LIST_COMPANY_MEMBERS);
        }

        log.info("Listing members of company {}", companyId);
        return this.companyMemberRepository.findByCompanyId(companyId);
    }

    @Transactional
    public Company suspendCompany(UUID companyId) throws RentlixApiException {
        Company company = this.requireAdminCompany(companyId, Features.SUSPEND_COMPANY);
        this.validatePreviousStatus(company, CompanyStatus.SUSPENDED, Features.SUSPEND_COMPANY);

        company.setStatus(CompanyStatus.SUSPENDED);
        company.setUpdatedAt(this.dateComponent.now());
        this.companyRepository.persist(company);

        log.info("Company suspended: {}", company.getCompanyId());
        return company;
    }

    @Transactional
    public Company cancelCompany(UUID companyId) throws RentlixApiException {
        Company company = this.requireAdminCompany(companyId, Features.CANCEL_COMPANY);
        this.validatePreviousStatus(company, CompanyStatus.CANCELED, Features.CANCEL_COMPANY);

        company.setStatus(CompanyStatus.CANCELED);
        company.setUpdatedAt(this.dateComponent.now());
        this.companyRepository.persist(company);

        log.info("Company canceled: {}", company.getCompanyId());
        return company;
    }

    @Transactional
    public Company reactivateCompany(UUID companyId) throws RentlixApiException {
        Company company = this.requireAdminCompany(companyId, Features.REACTIVATE_COMPANY);
        this.validatePreviousStatus(company, CompanyStatus.ACTIVE, Features.REACTIVATE_COMPANY);

        company.setStatus(CompanyStatus.ACTIVE);
        company.setUpdatedAt(this.dateComponent.now());
        this.companyRepository.persist(company);

        log.info("Company reactivated: {}", company.getCompanyId());
        return company;
    }

    protected void validatePreviousStatus(Company company, CompanyStatus nextStatus, Features feature) throws RentlixApiException {
        CompanyStatus currentStatus = company.getStatus();

        if (currentStatus.isFinalStatus()) {
            throw new CompanyStatusConflictException(feature);
        }
        if (!nextStatus.getValidPreviousStatuses().contains(currentStatus)) {
            throw new CompanyStatusConflictException(feature);
        }
    }

    protected Company requireAdminCompany(UUID companyId, Features feature) throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException(feature);
        }

        Company company = this.companyRepository.findByIdOptional(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(feature));

        CompanyMember currentMember = this.companyMemberRepository.findByUserIdAndCompanyId(currentUser.getUserId(), companyId)
                .orElseThrow(() -> new AccessDeniedException(feature));

        if (currentMember.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException(feature);
        }

        return company;
    }

    protected Company requireMemberCompany(UUID companyId, Features feature) throws RentlixApiException {
        Company company = this.companyRepository.findByIdOptional(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(feature));
        this.requireCompanyMember(companyId, feature);
        return company;
    }

    protected void requireCompanyMember(UUID companyId, Features feature) throws RentlixApiException {
        ContextUser currentUser = this.contextComponent.getCurrentUser();
        if (currentUser == null) {
            throw new AccessDeniedException(feature);
        }
        if (!this.companyMemberRepository.existsByUserIdAndCompanyId(currentUser.getUserId(), companyId)) {
            throw new AccessDeniedException(feature);
        }
    }

    @Transactional
    public Company uploadLogo(UUID companyId, byte[] fileBytes) throws RentlixApiException {
        Company company = this.requireAdminCompany(companyId, Features.UPLOAD_COMPANY_LOGO);
        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new CompanyStatusConflictException(Features.UPLOAD_COMPANY_LOGO);
        }

        if (company.getLogoUrl() != null && !company.getLogoUrl().isBlank()) {
            String oldPublicId = this.cloudinaryService.extractPublicId(company.getLogoUrl());
            if (oldPublicId != null) {
                this.cloudinaryService.delete(oldPublicId, CloudinaryFolder.COMPANY_LOGO.getResourceType(), Features.UPLOAD_COMPANY_LOGO);
            }
        }

        String url = this.cloudinaryService.upload(fileBytes, company.getCompanyId().toString(), CloudinaryFolder.COMPANY_LOGO, Features.UPLOAD_COMPANY_LOGO);
        company.setLogoUrl(url);
        company.setUpdatedAt(this.dateComponent.now());
        this.companyRepository.persist(company);
        log.info("Logo uploaded for company: {}", company.getCompanyId());
        return company;
    }

    @Transactional
    public Company deleteLogo(UUID companyId) throws RentlixApiException {
        Company company = this.requireAdminCompany(companyId, Features.DELETE_COMPANY_LOGO);
        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new CompanyStatusConflictException(Features.DELETE_COMPANY_LOGO);
        }
        if (company.getLogoUrl() == null || company.getLogoUrl().isBlank()) {
            throw new PictureNotFoundException(Features.DELETE_COMPANY_LOGO);
        }

        String publicId = this.cloudinaryService.extractPublicId(company.getLogoUrl());
        if (publicId != null) {
            this.cloudinaryService.delete(publicId, CloudinaryFolder.COMPANY_LOGO.getResourceType(), Features.DELETE_COMPANY_LOGO);
        }
        company.setLogoUrl(null);
        company.setUpdatedAt(this.dateComponent.now());
        this.companyRepository.persist(company);
        log.info("Logo deleted for company: {}", companyId);
        return company;
    }

    @Transactional
    public PublicCompany getPublicCompany(String slug, int page, int size) throws RentlixApiException {
        Company company = this.companyRepository.findBySlug(slug)
                .orElseThrow(() -> new CompanyNotFoundException(Features.GET_PUBLIC_COMPANY));
        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new CompanyNotFoundException(Features.GET_PUBLIC_COMPANY);
        }

        PageWindow window = this.pageWindow(page, size);
        List<Asset> assets = this.assetRepository.findPublicPageByCompany(company.getCompanyId(), window.page(), window.size());
        long totalElements = this.assetRepository.countPublicByCompany(company.getCompanyId());
        return new PublicCompany(company, new AssetPage(assets, window.page(), window.size(), totalElements));
    }

    private String slug(String name) {
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        if (base.length() > 150) {
            base = base.substring(0, 150).replaceAll("-+$", "");
        }
        if (base.isBlank()) {
            base = "company";
        }
        String slug = base;
        int suffix = 2;
        while (this.companyRepository.existsBySlug(slug)) {
            slug = base + "-" + suffix;
            suffix++;
        }
        return slug;
    }

    private PageWindow pageWindow(int page, int size) {
        int currentPage = page;
        int pageSize = size;
        if (currentPage < 0) {
            currentPage = 0;
        }
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        return new PageWindow(currentPage, pageSize);
    }

    private record PageWindow(int page, int size) {}
}
