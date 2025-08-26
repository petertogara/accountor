package com.accountor.prh.client;

import com.accountor.prh.api.PrhClient;
import com.accountor.prh.dto.CompanyResultApiDto;
import com.accountor.prh.exception.CompanyNotFoundException;
import com.accountor.prh.exception.ExternalApiException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PrhClientImpl implements PrhClient {

    private final WebClient prhWebClient;
    private final Logger log = LoggerFactory.getLogger(PrhClientImpl.class);

    @Override
    @Retry(name = "prhService", fallbackMethod = "retryFallback")
    @CircuitBreaker(name = "prhService", fallbackMethod = "circuitBreakerFallback")
    @RateLimiter(name = "prhService", fallbackMethod = "rateLimiterFallback")
    public Mono<CompanyResultApiDto> getCompaniesByBusinessId(String businessId) {
        log.info("Attempting to get company details for businessId: {}", businessId);

        return prhWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/companies")
                        .queryParam("businessId", businessId)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Client error from PRH API. Status: {}, Body: {}",
                                            clientResponse.statusCode(), errorBody);
                                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                                        return Mono.error(new CompanyNotFoundException("Company not found for business ID: " + businessId));
                                    }
                                    if (clientResponse.statusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                        return Mono.error(new ExternalApiException("Too many requests to external API.", String.valueOf(clientResponse.statusCode().value()), errorBody));
                                    }
                                    return Mono.error(new ExternalApiException(
                                            "Client error from external API: " + clientResponse.statusCode().value(),
                                            String.valueOf(clientResponse.statusCode().value()),
                                            errorBody));
                                }))

                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new ExternalApiException(
                                "Server error from external API: " + clientResponse.statusCode().value(),
                                String.valueOf(clientResponse.statusCode().value()),
                                "The external service is likely down or unstable.")))
                .bodyToMono(CompanyResultApiDto.class);
    }

    private Mono<CompanyResultApiDto> retryFallback(String businessId, Throwable ex) {
        log.error("All retries exhausted for businessId: {}. Falling back. Cause: {}", businessId, ex.getMessage());
        return Mono.error(new ExternalApiException("PRH API is not responding after multiple attempts. Please check the service status.", ex));
    }

    private Mono<CompanyResultApiDto> circuitBreakerFallback(String businessId, Throwable ex) {
        log.error("Circuit breaker is open for businessId: {}. Falling back. Cause: {}", businessId, ex.getMessage());
        return Mono.error(new ExternalApiException("PRH API is currently unavailable. The circuit is open.", ex));
    }


    private Mono<CompanyResultApiDto> rateLimiterFallback(String businessId, Throwable ex) {
        log.error("Rate limit exceeded for businessId: {}. Falling back. Cause: {}", businessId, ex.getMessage());
        return Mono.error(new ExternalApiException("Rate limit for PRH API has been exceeded. Please try again later.", ex));
    }
}