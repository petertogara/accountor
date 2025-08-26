package com.accountor.prh.controller;

import com.accountor.prh.domain.CompanyDetailsDto;
import com.accountor.prh.service.PrhService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v1/prh/companies")
@Tag(name = "Company", description = "Endpoints for retrieving company data from the PRH API.")
public class CompanyController {
    private final PrhService prhService;

    public CompanyController(PrhService prhService) {
        this.prhService = prhService;
    }


    @Operation(
            summary = "Retrieve company details by business ID",
            description = "Fetches comprehensive details for a company using its Finnish business ID (y-tunnus).",
            tags = {"Company"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "Company details successfully retrieved.",
            content = @Content(schema = @Schema(implementation = CompanyDetailsDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid business ID format.",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Company not found for the given business ID.",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "An internal server error occurred.",
            content = @Content(schema = @Schema(implementation = String.class))
    )
    @GetMapping("/{businessId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CompanyDetailsDto> getCompanyDetails(@PathVariable String businessId) {
        return prhService.getCompanyDetails(businessId);
    }
}
