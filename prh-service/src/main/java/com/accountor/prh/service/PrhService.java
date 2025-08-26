package com.accountor.prh.service;

import com.accountor.prh.domain.CompanyDetailsDto;
import reactor.core.publisher.Mono;

public interface PrhService {

    Mono<CompanyDetailsDto> getCompanyDetails(String businessId);
}
