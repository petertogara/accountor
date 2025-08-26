package com.accountor.prh;

import okhttp3.mockwebserver.MockWebServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;

@TestConfiguration
public class TestcontainersConfiguration {

    @Bean
    public MockWebServer mockPrhApi() throws IOException {
        MockWebServer server = new MockWebServer();
        server.start();
        return server;
    }

    @DynamicPropertySource
    static void registerMockWebServerProperties(DynamicPropertyRegistry registry) {

        registry.add("prh.api.base-url", () -> "http://localhost:8089/");
    }
}