package com.accountor.prh.client;

import com.accountor.prh.exception.CompanyNotFoundException;
import com.accountor.prh.exception.ExternalApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class PrhClientImplTests {

    private static MockWebServer mockPrhApi;
    private PrhClientImpl prhClient;

    @BeforeAll
    static void setUpAll() throws IOException {
        mockPrhApi = new MockWebServer();
        mockPrhApi.start();
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        mockPrhApi.shutdown();
    }

    @BeforeEach
    void setUp() {

        String baseUrl = String.format("http://localhost:%s", mockPrhApi.getPort());
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        prhClient = new PrhClientImpl(webClient);
    }

    @Test
    @DisplayName("Should return company details when API returns 200 OK")
    void getCompaniesByBusinessId_shouldReturnCompany_whenApiReturns200() {

        mockPrhApi.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"totalResults\":1, \"companies\":[{\"businessId\":{\"value\":\"123456-7\"}}]}"));


        StepVerifier.create(prhClient.getCompaniesByBusinessId("123456-7"))
                .expectNextMatches(dto -> dto.totalResults() == 1 && dto.results().get(0).businessId().value().equals("123456-7"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when API returns 404 Not Found")
    void getCompaniesByBusinessId_shouldThrowCompanyNotFound_whenApiReturns404() {
        String businessId = "non-existent";

        mockPrhApi.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"message\":\"Company not found\"}"));

        StepVerifier.create(prhClient.getCompaniesByBusinessId(businessId))
                .expectError(CompanyNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should throw ExternalApiException when API returns 400 Bad Request")
    void getCompaniesByBusinessId_shouldThrowExternalApi_whenApiReturns400() {

        mockPrhApi.enqueue(new MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"timestamp\":\"\",\"message\":\"Bad Request\",\"errorcode\":0}"));

        StepVerifier.create(prhClient.getCompaniesByBusinessId("bad-input"))
                .expectErrorMatches(e -> e instanceof ExternalApiException && e.getMessage().contains("Client error from external API: 400"))
                .verify();
    }

    @Test
    @DisplayName("Should throw ExternalApiException when API returns 429 Too Many Requests")
    void getCompaniesByBusinessId_shouldThrowExternalApi_whenApiReturns429() {
        mockPrhApi.enqueue(new MockResponse()
                .setResponseCode(429)
                .addHeader("Content-Type", "text/plain")
                .setBody("Too many requests, try again later."));

        StepVerifier.create(prhClient.getCompaniesByBusinessId("rate-limited"))
                .expectErrorMatches(e -> e instanceof ExternalApiException && e.getMessage().contains("Too many requests to external API."))
                .verify();
    }

    @Test
    @DisplayName("Should throw ExternalApiException when API returns 500 Internal Server Error")
    void getCompaniesByBusinessId_shouldThrowExternalApi_whenApiReturns500() {
        mockPrhApi.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"timestamp\":\"\",\"message\":\"Internal Server Error\",\"errorcode\":0}"));

        StepVerifier.create(prhClient.getCompaniesByBusinessId("server-error"))
                .expectErrorMatches(e -> e instanceof ExternalApiException && e.getMessage().contains("Server error from external API: 500"))
                .verify();
    }
}