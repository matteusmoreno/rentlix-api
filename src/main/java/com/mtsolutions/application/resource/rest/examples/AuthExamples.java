package com.mtsolutions.application.resource.rest.examples;

public class AuthExamples {

    public static final String LOGIN_REQUEST_EXAMPLE = """
            {
                "email": "joao.silva@email.com",
                "password": "senha@123"
            }
            """;

    public static final String FORGOT_PASSWORD_EXAMPLE = """
            {
                "email": "joao.silva@email.com"
            }
            """;

    public static final String RESET_PASSWORD_EXAMPLE = """
            {
                "email": "joao.silva@email.com",
                "code": "A1B2C3",
                "password": "senha@123"
            }
            """;

    private  AuthExamples() {}
}
