package com.accountor.prh.utils;

import com.accountor.prh.exception.InvalidInputException;
import org.springframework.stereotype.Component;

@Component
public class PrhValidator {

    private static final String BUSINESS_ID_REGEX = "^\\d{7}-\\d{1}$";

    /**
     * Checks if the provided business ID is in a valid format.
     *
     * @param businessId The business ID to validate.
     * @return false if the business ID is null or does not match the required regex format.
     */

    public boolean validate(String businessId) {
        return businessId != null && businessId.matches(BUSINESS_ID_REGEX);
    }

}