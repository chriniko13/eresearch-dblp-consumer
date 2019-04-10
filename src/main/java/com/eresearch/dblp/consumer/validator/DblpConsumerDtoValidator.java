package com.eresearch.dblp.consumer.validator;

import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.error.EresearchDblpConsumerError;
import com.eresearch.dblp.consumer.exception.DataValidationException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

/*
NOTE: only initials can be null or empty.
 */
@Component
@Log4j
public class DblpConsumerDtoValidator implements Validator<DblpConsumerDto> {

    @Override
    public void validate(DblpConsumerDto dblpConsumerDto) throws DataValidationException {

        if (Objects.isNull(dblpConsumerDto)) {
            log.error("DblpConsumerDtoValidator#validate --- error occurred (first validation) --- dblpConsumerDto = " + dblpConsumerDto);
            throw new DataValidationException(EresearchDblpConsumerError.DATA_VALIDATION_ERROR, EresearchDblpConsumerError.DATA_VALIDATION_ERROR.getMessage());
        }

        validateAuthorName(dblpConsumerDto);
    }

    private void validateAuthorName(DblpConsumerDto dblpConsumerDto) throws DataValidationException {

        if (orReducer(
                isNull(dblpConsumerDto.getFirstname()),
                isNull(dblpConsumerDto.getSurname()),
                isEmpty(dblpConsumerDto.getFirstname()),
                isEmpty(dblpConsumerDto.getSurname()))) {
            log.error("DblpConsumerDtoValidator#validate --- error occurred (second validation) --- dblpConsumerDto = " + dblpConsumerDto);
            throw new DataValidationException(EresearchDblpConsumerError.DATA_VALIDATION_ERROR, EresearchDblpConsumerError.DATA_VALIDATION_ERROR.getMessage());
        }

    }

    private Boolean orReducer(Boolean... booleans) {
        return Arrays.stream(booleans).reduce(false, (acc, elem) -> acc || elem);
    }

    private Boolean isEmpty(String datum) {
        return "".equals(datum);
    }

    private <T> Boolean isNull(T datum) {
        return Objects.isNull(datum);
    }
}
