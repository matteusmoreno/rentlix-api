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
                "password": "senha123",
                "pictureUrl": "https://example.com/pictures/ana-souza.jpg",
                "userDocument": {
                    "cnhUrl": "https://example.com/documents/cnh.pdf",
                    "cpfUrl": "https://example.com/documents/cpf.pdf",
                    "rgUrl": "https://example.com/documents/rg.pdf",
                    "proofOfResidenceUrl": "https://example.com/documents/comprovante.pdf",
                    "criminalRecordUrl": "https://example.com/documents/antecedentes.pdf",
                    "passportUrl": "https://example.com/documents/passaporte.pdf"
                }
            }
            """;

    private UserResourceExamples() {}
}
