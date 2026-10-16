package com.mtsolutions.application.resource.rest.examples;

public class CompanyResourceExamples {

    public static final String CREATE_COMPANY_EXAMPLE = """
            {
                "name": "Locadora Paulista",
                "cnpj": "12.345.678/0001-90",
                "plan": "FREE"
            }
            """;

    public static final String ADD_COMPANY_MEMBER_EXAMPLE = """
            {
                "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "role": "LESSEE"
            }
            """;

    private CompanyResourceExamples() {}
}
