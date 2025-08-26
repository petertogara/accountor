package com.accountor.prh.api;

import com.accountor.prh.dto.CompanyResultApiDto;
import reactor.core.publisher.Mono;

public interface PrhClient {

    Mono<CompanyResultApiDto> getCompaniesByBusinessId(String businessId);

}