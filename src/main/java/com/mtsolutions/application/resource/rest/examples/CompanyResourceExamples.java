package com.mtsolutions.application.resource.rest.examples;

public class CompanyResourceExamples {

    public static final String CREATE_COMPANY_EXAMPLE = """
            {
                "name": "Locadora Paulista",
                "cnpj": "12.345.678/0001-90"
            }
            """;

    public static final String UPDATE_COMPANY_EXAMPLE = """
            {
                "name": "Locadora Paulista",
                "slug": "locadora-paulista"
            }
            """;

    public static final String ADD_COMPANY_MEMBER_EXAMPLE = """
            {
                "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "role": "LESSEE"
            }
            """;

    public static final String SAVE_CONTRACT_TEMPLATE_EXAMPLE = """
            {
                "content": "<p>Locador: {{company.name}}, CNPJ {{company.cnpj}}.</p><p>Locatário: {{lessee.name}}, CPF {{lessee.cpf}}.</p><p>Bem: {{asset.name}}. De {{rental.startDate}} a {{rental.endDate}}, no valor de {{rental.price}} por {{rental.pricePeriod}}.</p>"
            }
            """;

    private CompanyResourceExamples() {}
}
