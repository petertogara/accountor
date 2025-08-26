package com.accountor.prh.service.impl;

import com.accountor.prh.api.PrhClient;
import com.accountor.prh.domain.CompanyDetailsDto;
import com.accountor.prh.domain.CompanyMapper;
import com.accountor.prh.exception.CompanyNotFoundException;
import com.accountor.prh.exception.InvalidInputException;
import com.accountor.prh.exception.MappingException;
import com.accountor.prh.service.PrhService;
import com.accountor.prh.utils.PrhValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class PrhServiceImpl implements PrhService {

    private final PrhClient prhClient;
    private final PrhValidator validator;
    private final Logger log = LoggerFactory.getLogger(PrhServiceImpl.class);

    /**
     * Retrieves company details for a given business ID.
     *
     * @param businessId The Finnish Business ID (y-tunnus) to search for.
     * @return A Mono emitting CompanyDetailsDto if found, or an error if not found or an API issue occurs.
     * @throws CompanyNotFoundException If no company is found for the given business ID.
     * @throws MappingException         If there's an issue mapping the external API response to our domain.
     */
    @Override
    public Mono<CompanyDetailsDto> getCompanyDetails(String businessId) {
        log.info("Requesting details for business ID: {}", businessId);

            if (!validator.validate(businessId)) {
                return Mono.error(
                        new InvalidInputException("Business ID must be in the format XXXXXXX-X.")
                );
            }


            return prhClient.getCompaniesByBusinessId(businessId)

                    .publishOn(Schedulers.boundedElastic())

                    .flatMap(response -> {

                        if (response == null || response.totalResults() == 0) {
                            log.info("No company data or empty list found for business ID: {}", businessId);
                            return Mono.error(
                                    new CompanyNotFoundException("Company not found for business ID: " + businessId));
                        }

                        return Mono.justOrEmpty(response.results().stream().findFirst())
                                .map(CompanyMapper::toCompanyDetails)
                                .map(CompanyMapper::toCompanyDetailsDto)
                                .doOnNext(details ->
                                        log.info("Successfully mapped company details for businessId: {}", details.businessId()))
                                .switchIfEmpty(Mono.error(
                                        new MappingException(
                                                "API returned non-empty totalResults but an empty company list for business ID: " + businessId)));
                    });
    }


}