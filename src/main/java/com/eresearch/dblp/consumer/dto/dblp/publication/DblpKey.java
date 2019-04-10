package com.eresearch.dblp.consumer.dto.dblp.publication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class DblpKey {

    @XmlValue
    private String value;

    @XmlAttribute(name = "type")
    private String type;
}
