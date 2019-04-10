package com.eresearch.dblp.consumer.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DblpQueueResultDto {

    private String transactionId;
    private String exceptionMessage;
    private DblpResultsDto dblpResultsDto;
}
