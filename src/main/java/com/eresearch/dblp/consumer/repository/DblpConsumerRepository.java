package com.eresearch.dblp.consumer.repository;


import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.DblpResultsDto;

public interface DblpConsumerRepository {
    void save(DblpConsumerDto dblpConsumerDto, DblpResultsDto dblpResultsDto);
}
