package com.eresearch.dblp.consumer.dto.dblp.publication;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "dblpperson")
@XmlAccessorType(XmlAccessType.FIELD)
public class DblpPerson {

    @XmlAttribute(name = "name")
    private String authorName;

    @XmlElement(name = "dblpkey")
    private List<DblpKey> dblpKeys;
}
