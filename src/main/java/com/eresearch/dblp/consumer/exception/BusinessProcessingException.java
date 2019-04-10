package com.eresearch.dblp.consumer.exception;

import com.eresearch.dblp.consumer.error.EresearchDblpConsumerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class BusinessProcessingException extends Exception {

    private static final long serialVersionUID = -4352150800142237666L;

    private final EresearchDblpConsumerError eresearchDblpConsumerError;

    public BusinessProcessingException(EresearchDblpConsumerError eresearchDblpConsumerError, String message, Throwable cause) {
        super(message, cause);
        this.eresearchDblpConsumerError = eresearchDblpConsumerError;
    }

    public BusinessProcessingException(EresearchDblpConsumerError eresearchDblpConsumerError, String message) {
        super(message);
        this.eresearchDblpConsumerError = eresearchDblpConsumerError;
    }

    public EresearchDblpConsumerError getEresearchDblpConsumerError() {
        return eresearchDblpConsumerError;
    }
}
