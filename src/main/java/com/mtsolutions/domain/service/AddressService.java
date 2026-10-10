package com.mtsolutions.domain.service;

import com.mtsolutions.application.client.ViaCepClient;
import com.mtsolutions.application.client.dto.ViaCepResponseDto;
import com.mtsolutions.application.common.Features;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.application.exception.ViaCepUnavailableException;
import com.mtsolutions.application.exception.ZipCodeNotFoundException;
import com.mtsolutions.domain.dto.request.CreateAddressRequestDto;
import com.mtsolutions.domain.model.Address;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Slf4j
public class AddressService {

    private final ViaCepClient viaCepClient;

    public AddressService(@RestClient ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public Address getAddressFromZipcode(String zipCode, String number, String complement) throws RentlixApiException {
        log.info("Fetching address from ViaCEP for CEP: {}", zipCode);

        ViaCepResponseDto response;
        try {
            response = this.viaCepClient.getAddress(zipCode);
        } catch (WebApplicationException | ProcessingException exception) {
            log.error("Failed to fetch address from ViaCEP for CEP: {}", zipCode, exception);
            throw new ViaCepUnavailableException(Features.GET_ADDRESS);
        }

        if (response == null || !response.isValid()) {
            throw new ZipCodeNotFoundException(Features.GET_ADDRESS);
        }

        return Address.builder()
                .zipCode(response.getZipCode())
                .street(response.getStreet())
                .neighborhood(response.getNeighborhood())
                .city(response.getCity())
                .state(response.getState())
                .number(number)
                .complement(complement)
                .build();
    }

    public Address getAddress(CreateAddressRequestDto request) {
        log.info("Fetching address for CEP: {}", request.cep());

        return Address.builder()
                .zipCode(request.cep())
                .street(request.street())
                .number(request.number())
                .complement(request.complement())
                .neighborhood(request.neighborhood())
                .city(request.city())
                .state(request.state())
                .build();
    }
}
