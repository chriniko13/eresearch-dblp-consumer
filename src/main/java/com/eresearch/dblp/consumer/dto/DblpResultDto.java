package com.eresearch.dblp.consumer.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DblpResultDto {

    private List<String> authors;
    private String title;
    private String pages;
    private String year;
    private String volume;
    private String journal;
    private String number;
    private String bookTitle;
    private String ee;
    private String crossRef;

}
