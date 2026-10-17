package com.mtsolutions.application.resource.rest.examples;

public class FinancialResourceExamples {

    public static final String CREATE_FINANCIAL_EXAMPLE = """
            {
                "companyId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "assetId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "rentalId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "direction": "INCOME",
                "type": "RENT",
                "amount": 1500.00,
                "description": "October rent",
                "occurredOn": "2026-10-17"
            }
            """;

    private FinancialResourceExamples() {}
}
