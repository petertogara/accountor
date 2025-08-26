package com.accountor.prh.controller;

import com.accountor.prh.domain.CompanyDetailsDto;
import com.accountor.prh.domain.CompanyMapper;
import com.accountor.prh.dto.*;
import com.accountor.prh.exception.CompanyNotFoundException;
import com.accountor.prh.exception.ExternalApiException;
import com.accountor.prh.exception.GlobalExceptionHandler;
import com.accountor.prh.exception.MappingException;
import com.accountor.prh.service.PrhService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(CompanyController.class)
@Import(GlobalExceptionHandler.class)
class CompanyControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PrhService prhService;

    @Test
    @DisplayName("Should return 200 OK with company details when service is successful")
    void getCompanyDetails_shouldReturn200_whenServiceSucceeds() {

        String businessIdValue = "1234567-8";
        String companyNameValue = "Test Company";
        String registrationDateValue = "2020-01-01";

        BusinessIdApiDto mockBusinessId = new BusinessIdApiDto(businessIdValue, registrationDateValue);
        NameApiDto mockName = new NameApiDto(companyNameValue, "1", 3);
        CompanyApiDto mockCompany = new CompanyApiDto(mockBusinessId,
                List.of(mockName),
                List.of(new AddressApiDto("1", "Hunyani Street", "02240", null)),
                null,
                null,
                null,
                null,
                null,
                new MainBusinessLineApiDto("29120", List.of(new DescriptionApiDto("3", "Manufacture of pumps and compressors"),
                        new DescriptionApiDto("2", "Tillv av pumpar och kompressorer"))),
                null,
                Optional.of(new WebsiteApiDto("wwww.test.com")));

        CompanyDetailsDto expectedDto = CompanyMapper.toCompanyDetailsDto(
                CompanyMapper.toCompanyDetails(mockCompany));

        when(prhService.getCompanyDetails(anyString()))
                .thenReturn(Mono.just(expectedDto));

        webTestClient.get().uri("/api/v1/prh/companies/{businessId}", businessIdValue)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CompanyDetailsDto.class)
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("Should return 404 Not Found with JSON ProblemDetail when service throws CompanyNotFoundException")
    void getCompanyDetails_shouldReturn404_whenServiceThrowsCompanyNotFound() {

        String businessId = "non-existent-id";
        String errorMessage = "Company not found for business ID: " + businessId;

        when(prhService.getCompanyDetails(anyString()))
                .thenReturn(Mono.error(new CompanyNotFoundException(errorMessage)));

        webTestClient.get().uri("/api/v1/prh/companies/{businessId}", businessId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.title").isEqualTo("Not Found")
                .jsonPath("$.detail").isEqualTo(errorMessage)
                .jsonPath("$.instance").exists();
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error with JSON ProblemDetail when service throws MappingException")
    void getCompanyDetails_shouldReturn500_whenServiceThrowsMappingException() {

        String businessId = "inconsistent-api-data";
        String errorMessage = "API returned non-empty totalResults but an empty company list for business ID: " + businessId;

        when(prhService.getCompanyDetails(anyString()))
                .thenReturn(Mono.error(new MappingException(errorMessage)));


        webTestClient.get().uri("/api/v1/prh/companies/{businessId}", businessId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.title").isEqualTo("Internal Server Error")
                .jsonPath("$.detail").isEqualTo(errorMessage)
                .jsonPath("$.instance").exists();
    }


    @Test
    @DisplayName("Should return 503 Service Unavailable with JSON ProblemDetail when service throws ExternalApiException")
    void getCompanyDetails_shouldReturn503_whenServiceThrowsExternalApiException() {

        String businessId = "external-api-error-id";
        String errorMessage = "External service is currently experiencing issues. Please try again later.";
        when(prhService.getCompanyDetails(anyString()))
                .thenReturn(Mono.error(new ExternalApiException("External API internal server error.", "500", "Upstream error")));

        webTestClient.get().uri("/api/v1/prh/companies/{businessId}", businessId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(503)
                .jsonPath("$.title").isEqualTo("Service Unavailable")
                .jsonPath("$.detail").isEqualTo(errorMessage)
                .jsonPath("$.instance").exists();
    }

    @Test
    @DisplayName("Should return 400 Bad Request with JSON ProblemDetail when service throws IllegalArgumentException")
    void getCompanyDetails_shouldReturn400_whenServiceThrowsIllegalArgumentException() {
        String businessId = "illegal-arg";
        String errorMessage = "Invalid argument provided.";

        when(prhService.getCompanyDetails(anyString()))
                .thenReturn(Mono.error(new IllegalArgumentException(errorMessage)));

        webTestClient.get().uri("/api/v1/prh/companies/{businessId}", businessId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.title").isEqualTo("Bad Request")
                .jsonPath("$.detail").isEqualTo(errorMessage)
                .jsonPath("$.instance").exists();
    }


    @Test
    @DisplayName("Should return 500 Internal Server Error with JSON ProblemDetail when service throws an unexpected exception")
    void getCompanyDetails_shouldReturn500_whenServiceThrowsUnexpectedException() {

        String businessId = "unexpected-error-id";
        String errorMessage = "An unexpected error occurred : Something truly unexpected happened!";

        when(prhService.getCompanyDetails(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Something truly unexpected happened!")));

        webTestClient.get().uri("/api/v1/prh/companies/{businessId}", businessId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.title").isEqualTo("Internal Server Error")
                .jsonPath("$.detail").isEqualTo(errorMessage)
                .jsonPath("$.instance").exists();
    }
}