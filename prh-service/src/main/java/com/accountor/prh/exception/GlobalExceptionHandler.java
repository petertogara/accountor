package com.accountor.prh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CompanyNotFoundException.class)
    public Mono<ProblemDetail> handleCompanyNotFoundException(CompanyNotFoundException ex, ServerWebExchange exchange) {
        return buildProblemDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange);
    }

    @ExceptionHandler(MappingException.class)
    public Mono<ProblemDetail> handleMappingException(MappingException ex, ServerWebExchange exchange) {
        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                exchange);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public Mono<ProblemDetail> handleCallNotPermittedException(CallNotPermittedException ex, ServerWebExchange exchange) {
        return buildProblemDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service temporarily unavailable due to external system failure.",
                exchange);
    }

    @ExceptionHandler(ExternalApiException.class)
    public Mono<ProblemDetail> handleExternalApiException(ExternalApiException ex, ServerWebExchange exchange) {
        return buildProblemDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "External service is currently experiencing issues. Please try again later.",
                exchange);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ProblemDetail> handleInvalidInputException(ServerWebInputException ex, ServerWebExchange exchange) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST,
                "Invalid request payload or parameters." + ex.getMessage(),
                exchange);
    }

    @ExceptionHandler(InvalidInputException.class)
    public Mono<ProblemDetail> handleInvalidInputException(InvalidInputException ex, ServerWebExchange exchange) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST,
                 ex.getMessage(),
                exchange);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ProblemDetail> handleGenericException(Exception ex, ServerWebExchange exchange) {
        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred : " + ex.getMessage(),
                exchange);
    }

    private Mono<ProblemDetail> buildProblemDetail(HttpStatus status, String message, ServerWebExchange exchange) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, message);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setInstance(exchange.getRequest().getURI());

        return Mono.just(problemDetail);
    }
}
