package com.eresearch.dblp.consumer.exception;

import com.eresearch.dblp.consumer.error.EresearchDblpConsumerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DataValidationException extends Exception {

    private static final long serialVersionUID = -4807690929767265288L;

    private final EresearchDblpConsumerError eresearchDblpConsumerError;

    public DataValidationException(EresearchDblpConsumerError eresearchDblpConsumerError, String message) {
        super(message);
        this.eresearchDblpConsumerError = eresearchDblpConsumerError;
    }

    public EresearchDblpConsumerError getEresearchDblpConsumerError() {
        return eresearchDblpConsumerError;
    }
}
