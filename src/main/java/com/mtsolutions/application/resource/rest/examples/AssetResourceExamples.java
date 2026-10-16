package com.mtsolutions.application.resource.rest.examples;

public class AssetResourceExamples {

    public static final String CREATE_ASSET_EXAMPLE = """
            {
                "companyId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                "type": "MOTORCYCLE",
                "name": "Honda CG 160",
                "description": "Moto econômica para o dia a dia",
                "price": 120.00,
                "pricePeriod": "DAY",
                "vehicle": {
                    "brand": "Honda",
                    "model": "CG 160",
                    "manufactureYear": 2023,
                    "modelYear": 2024,
                    "color": "Vermelha",
                    "plate": "ABC1D23",
                    "renavam": "12345678901",
                    "chassis": "9C2KC2210PR123456",
                    "engineCc": 160,
                    "mileage": 12000
                }
            }
            """;

    public static final String UPDATE_ASSET_EXAMPLE = """
            {
                "name": "Honda CG 160",
                "description": "Moto econômica para o dia a dia",
                "price": 150.00,
                "pricePeriod": "DAY",
                "vehicle": {
                    "brand": "Honda",
                    "model": "CG 160",
                    "manufactureYear": 2023,
                    "modelYear": 2024,
                    "color": "Vermelha",
                    "plate": "ABC1D23",
                    "renavam": "12345678901",
                    "chassis": "9C2KC2210PR123456",
                    "engineCc": 160,
                    "mileage": 15000
                }
            }
            """;

    private AssetResourceExamples() {}
}
