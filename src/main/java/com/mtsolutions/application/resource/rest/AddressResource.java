package com.mtsolutions.application.resource.rest;

import com.mtsolutions.application.common.RequestParam;
import com.mtsolutions.application.exception.RentlixApiException;
import com.mtsolutions.domain.controller.AddressController;
import com.mtsolutions.domain.model.Address;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@RequestScoped
@Path("/v1/addresses")
@Tag(name = "Address", description = "Operations related to addresses")
public class AddressResource {

    private final AddressController addressController;

    public AddressResource(AddressController addressController) {
        this.addressController = addressController;
    }

    @GET
    @PermitAll
    public Response getAddress(
            @QueryParam(RequestParam.ZIP_CODE) @NotBlank(message = "zipCode is required") @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP must follow the format 12345-678 or 12345678") String zipCode,
            @QueryParam(RequestParam.NUMBER) String number,
            @QueryParam(RequestParam.COMPLEMENT) String complement) throws RentlixApiException {

        Address address = this.addressController.getAddressByZipCode(zipCode, number, complement);

        return Response.status(Response.Status.OK)
                .entity(address)
                .build();
    }
}
