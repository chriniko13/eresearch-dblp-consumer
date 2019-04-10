package com.eresearch.dblp.consumer.dto.dblp.author;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "authors")
@XmlAccessorType(XmlAccessType.FIELD)
public class DblpAuthors {

    @XmlElement(name = "author")
    private List<DblpAuthor> authors;
}
