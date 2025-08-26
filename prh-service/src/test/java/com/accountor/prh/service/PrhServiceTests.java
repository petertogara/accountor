package com.accountor.prh.service;

import com.accountor.prh.api.PrhClient;
import com.accountor.prh.domain.CompanyDetailsDto;
import com.accountor.prh.domain.CompanyMapper;
import com.accountor.prh.dto.*;
import com.accountor.prh.exception.CompanyNotFoundException;
import com.accountor.prh.exception.ExternalApiException;
import com.accountor.prh.exception.InvalidInputException;
import com.accountor.prh.exception.MappingException;
import com.accountor.prh.service.impl.PrhServiceImpl;
import com.accountor.prh.utils.PrhValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrhServiceTests {

    @Mock
    private PrhClient prhClient;

    @InjectMocks
    private PrhServiceImpl prhService;

    @Mock
    private PrhValidator validator;

    @Test
    @DisplayName("Should return company details when client call is successful and data is valid")
    void getCompanyDetails_shouldReturnCompany_whenClientSucceeds() {

        String businessIdValue = "0123456-7";
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

        CompanyResultApiDto mockApiDto = new CompanyResultApiDto(1, List.of(mockCompany));

        CompanyDetailsDto expectedDto = CompanyMapper.toCompanyDetailsDto(
                CompanyMapper.toCompanyDetails(mockCompany));

        when(prhClient.getCompaniesByBusinessId(anyString()))
                .thenReturn(Mono.just(mockApiDto));

        when(validator.validate(anyString())).thenReturn(true);


        Mono<CompanyDetailsDto> result = prhService.getCompanyDetails(businessIdValue);


        StepVerifier.create(result)
                .expectNext(expectedDto)
                .verifyComplete();
    }


    @Test
    @DisplayName("Should propagate CompanyNotFoundException when client returns empty results")
    void getCompanyDetails_shouldPropagateCompanyNotFound_whenClientReturnsEmptyResults() {

        CompanyResultApiDto emptyResultsDto = new CompanyResultApiDto(0, Collections.emptyList());

        when(prhClient.getCompaniesByBusinessId(anyString()))
                .thenReturn(Mono.just(emptyResultsDto));

        when(validator.validate(anyString())).thenReturn(true);

        Mono<CompanyDetailsDto> result = prhService.getCompanyDetails(anyString());

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof CompanyNotFoundException &&
                        e.getMessage().equals("Company not found for business ID: " + anyString()))
                .verify();
    }

    @Test
    @DisplayName("Should propagate MappingException when API returns totalResults > 0 but empty company list")
    void getCompanyDetails_shouldPropagateMappingException_whenApiReturnsNonEmptyTotalButEmptyList() {

        CompanyResultApiDto inconsistentDto = new CompanyResultApiDto(1, Collections.emptyList());

        when(prhClient.getCompaniesByBusinessId(anyString()))
                .thenReturn(Mono.just(inconsistentDto));
        when(validator.validate(anyString())).thenReturn(true);

        Mono<CompanyDetailsDto> result = prhService.getCompanyDetails(anyString());

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof MappingException &&
                        e.getMessage().contains("API returned non-empty totalResults but an empty company list"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate CompanyNotFoundException when client returns 404")
    void getCompanyDetails_shouldPropagateCompanyNotFound_whenClientReturns404() {

        when(prhClient.getCompaniesByBusinessId(anyString()))
                .thenReturn(Mono.error(new CompanyNotFoundException("Company not found")));
        when(validator.validate(anyString())).thenReturn(true);

        Mono<CompanyDetailsDto> result = prhService.getCompanyDetails("non-existent-id");

        StepVerifier.create(result)
                .expectError(CompanyNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should propagate ExternalApiException for other client errors")
    void getCompanyDetails_shouldPropagateExternalApiError_whenClientReturnsOther4xx() {

        when(prhClient.getCompaniesByBusinessId(anyString()))
                .thenReturn(Mono.error(new ExternalApiException("Bad Request", "400", "Invalid business ID Sequence")));
        when(validator.validate(anyString())).thenReturn(true);

        Mono<CompanyDetailsDto> result = prhService.getCompanyDetails(anyString());

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof ExternalApiException && e.getMessage().contains("Bad Request"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate ExternalApiException for server errors")
    void getCompanyDetails_shouldPropagateExternalApiError_whenClientReturns5xx() {

        when(prhClient.getCompaniesByBusinessId(anyString()))
                .thenReturn(Mono.error(new ExternalApiException("Internal Server Error", "500", "Upstream server issue")));

        when(validator.validate(anyString())).thenReturn(true);



        Mono<CompanyDetailsDto> result = prhService.getCompanyDetails(anyString());

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof ExternalApiException && e.getMessage().contains("Internal Server Error"))
                .verify();
    }

    @Test
    @DisplayName("Should return InvalidInputException for invalid business ID format")
    void getCompanyDetails_shouldThrowException_whenBusinessIdIsInvalid() {
        String invalidBusinessId = "invalid-id";

        when(validator.validate(invalidBusinessId)).thenReturn(false);

        Mono<CompanyDetailsDto> result = prhService.getCompanyDetails(invalidBusinessId);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof InvalidInputException &&
                        e.getMessage().equals("Business ID must be in the format XXXXXXX-X."))
                .verify();
    }

}

