package com.eresearch.dblp.consumer.connector.worker.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class FetcherWorkersConfiguration {

    @Value("${dblp.authors.list.partition}")
    private Integer numberOfDblpAuthorFetcherWorkersToCreate;

    @Value("${dblp.entries.list.partition}")
    private Integer numberOfDblpEntryFetcherWorkersToCreate;
}
