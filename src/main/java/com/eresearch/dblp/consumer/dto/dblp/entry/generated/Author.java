//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.27 at 11:20:24 PM EET 
//


package com.eresearch.dblp.consumer.dto.dblp.entry.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "value"
})
@XmlRootElement(name = "author")
public class Author {

    @XmlAttribute(name = "aux")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String aux;

    @XmlAttribute(name = "bibtex")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String bibtex;

    @XmlAttribute(name = "orcid")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String orcid;

    @XmlValue
    protected String value;

}
