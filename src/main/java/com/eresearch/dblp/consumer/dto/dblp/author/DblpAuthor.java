package com.eresearch.dblp.consumer.dto.dblp.author;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@EqualsAndHashCode(of = {"authorName", "urlpt"})
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class DblpAuthor {

    @XmlValue
    private String authorName;

    @XmlAttribute(name = "urlpt")
    private String urlpt;

    @Override
    public String toString() {
        return "authorName=" + authorName + "#urlpt=" + urlpt;
    }
}
