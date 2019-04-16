package com.eresearch.dblp.consumer.dto;


import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonDeserialize(keyUsing = FetchedResultsKeyDeserializer.class)
    private Map<DblpAuthor, List<Dblp>> results;


    public static class FetchedResultsKeyDeserializer extends KeyDeserializer {

        @Override
        public DblpAuthor deserializeKey(String key, DeserializationContext ctxt) {
            // Example of a key: authorName=Christos Skourlas#urlpt=s/Skourlas:Christos
            String[] data = key.split("#");

            DblpAuthor dblpAuthor = new DblpAuthor();
            dblpAuthor.setAuthorName(data[0]);
            dblpAuthor.setUrlpt(data[1]);

            return dblpAuthor;
        }
    }
}
