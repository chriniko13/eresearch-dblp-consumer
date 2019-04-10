package com.eresearch.dblp.consumer.dto;


import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class DblpResultsDto {

    @JsonProperty("operation-result")
    private Boolean operationResult;

    @JsonProperty("process-finished-date")
    private Instant processFinishedDate;

    @JsonProperty("requested-dblp-consumer-dto")
    private DblpConsumerDto dblpConsumerDto;

    @JsonProperty("fetched-results-size")
    private Integer resultsSize;

    @JsonProperty("fetched-results")
    private Map<DblpAuthor, List<Dblp>> results;
}
