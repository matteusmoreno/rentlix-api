package com.mtsolutions.domain.controller;

import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.model.Address;
import com.mtsolutions.domain.service.AddressService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    public Address getAddressByZipCode(String zipCode, String number, String complement) throws RentlixApiException {
        return this.addressService.getAddressFromZipcode(zipCode, number, complement);
    }
}
