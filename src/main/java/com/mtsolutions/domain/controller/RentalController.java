package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.dto.request.CreateRentalRequestDto;
import com.mtsolutions.domain.dto.request.RenewRentalRequestDto;
import com.mtsolutions.domain.entity.Rental;
import com.mtsolutions.domain.model.RentalPage;
import com.mtsolutions.domain.service.RentalService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    public Rental createRental(CreateRentalRequestDto request) throws RentlixApiException {
        return this.rentalService.createRental(request);
    }

    public Rental finishRental(UUID rentalId) throws RentlixApiException {
        return this.rentalService.finishRental(rentalId);
    }

    public Rental renewRental(UUID rentalId, RenewRentalRequestDto request) throws RentlixApiException {
        return this.rentalService.renewRental(rentalId, request);
    }

    public RentalPage listRentals(UUID companyId, int page, int size) throws RentlixApiException {
        return this.rentalService.listRentals(companyId, page, size);
    }

    public Rental getRental(UUID rentalId) throws RentlixApiException {
        return this.rentalService.getRental(rentalId);
    }

    public Rental uploadContract(UUID rentalId, byte[] fileBytes) throws RentlixApiException {
        return this.rentalService.uploadContract(rentalId, fileBytes);
    }

    public Rental deleteContract(UUID rentalId) throws RentlixApiException {
        return this.rentalService.deleteContract(rentalId);
    }
}
