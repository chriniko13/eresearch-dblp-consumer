package com.eresearch.dblp.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DblpConsumerDto {

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("initials")
    private String initials;

    @JsonProperty("surname")
    private String surname;

}
