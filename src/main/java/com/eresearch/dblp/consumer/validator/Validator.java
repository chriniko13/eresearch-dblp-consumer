package com.eresearch.dblp.consumer.validator;


import com.eresearch.dblp.consumer.exception.DataValidationException;

public interface Validator<T> {

    void validate(T data) throws DataValidationException;
}
