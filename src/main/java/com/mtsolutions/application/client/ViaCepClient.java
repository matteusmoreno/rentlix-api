package com.mtsolutions.application.client;

import com.mtsolutions.application.client.dto.ViaCepResponseDto;
import com.mtsolutions.application.common.RequestParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "via-cep")
public interface ViaCepClient {


    @GET
    @Path("/ws/{cep}/json/")
    ViaCepResponseDto getAddress(@PathParam(RequestParam.CEP) String cep);
}
