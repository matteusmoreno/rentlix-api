package com.mtsolutions.application.resource.rest.examples;

public class UserResourceExamples {

    public static final String CREATE_NEW_USER_EXAMPLE = """
            {
                "name": "Ana Souza",
                "birthDate": "1992-04-15",
                "phone": "(11)98888-7766",
                "cpf": "529.982.247-25",
                "rg": "12.345.678-9",
                "occupation": "Analista",
                "maritalStatus": "SINGLE",
                "address": {
                    "cep": "01310-100",
                    "street": "Avenida Paulista",
                    "neighborhood": "Bela Vista",
                    "city": "São Paulo",
                    "state": "SP",
                    "number": "1000",
                    "complement": "Apto 42"
                },
                "email": "ana.souza@email.com",
                "password": "senha123"
            }
            """;

    private UserResourceExamples() {}
}
