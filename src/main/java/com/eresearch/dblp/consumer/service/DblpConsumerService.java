package com.eresearch.dblp.consumer.service;

import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.DblpResultsDto;
import com.eresearch.dblp.consumer.exception.BusinessProcessingException;

public interface DblpConsumerService {

    DblpResultsDto dblpConsumerOperation(DblpConsumerDto dblpConsumerDto) throws BusinessProcessingException;

    void dblpNonBlockConsumption(String transactionId, DblpConsumerDto dblpConsumerDto);
}
