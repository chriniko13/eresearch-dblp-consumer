package com.eresearch.dblp.consumer.resource;


import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.DblpImmediateResultDto;
import com.eresearch.dblp.consumer.dto.DblpResultsDto;
import com.eresearch.dblp.consumer.exception.BusinessProcessingException;
import com.eresearch.dblp.consumer.exception.DataValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public interface DblpConsumerResource {

    DeferredResult<DblpResultsDto> dblpConsumerOperation(DblpConsumerDto dblpConsumerDto) throws BusinessProcessingException;

    ResponseEntity<DblpImmediateResultDto> dblpNonBlockConsumption(String transactionId, DblpConsumerDto dblpConsumerDto) throws DataValidationException;
}
