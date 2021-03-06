//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.02.27 at 11:20:24 PM EET 
//


package com.eresearch.dblp.consumer.dto.dblp.entry.generated;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "incollection")
public class Incollection {

    @XmlAttribute(name = "key", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String key;

    @XmlAttribute(name = "mdate")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String mdate;

    @XmlAttribute(name = "publtype")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String publtype;

    @XmlAttribute(name = "cdate")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String cdate;

    @XmlElement(name = "author", type = Author.class)
    private List<Author> authors;

    @XmlElement(name = "editor", type = Editor.class)
    private List<Editor> editors;

    @XmlElement(name = "title", type = Title.class)
    private List<Title> titles;

    @XmlElement(name = "booktitle", type = Booktitle.class)
    private List<Booktitle> booktitles;

    @XmlElement(name = "pages", type = Pages.class)
    private List<Pages> pages;

    @XmlElement(name = "year", type = Year.class)
    private List<Year> years;

    @XmlElement(name = "address", type = Address.class)
    private List<Address> addresses;

    @XmlElement(name = "journal", type = Journal.class)
    private List<Journal> journals;

    @XmlElement(name = "volume", type = Volume.class)
    private List<Volume> volumes;

    @XmlElement(name = "number", type = Number.class)
    private List<Number> numbers;

    @XmlElement(name = "month", type = Month.class)
    private List<Month> months;

    @XmlElement(name = "url", type = Url.class)
    private List<Url> urls;

    @XmlElement(name = "ee", type = Ee.class)
    private List<Ee> ees;

    @XmlElement(name = "cdrom", type = Cdrom.class)
    private List<Cdrom> cdroms;

    @XmlElement(name = "cite", type = Cite.class)
    private List<Cite> cites;

    @XmlElement(name = "publisher", type = Publisher.class)
    private List<Publisher> publishers;

    @XmlElement(name = "note", type = Note.class)
    private List<Note> notes;

    @XmlElement(name = "crossref", type = Crossref.class)
    private List<Crossref> crossrefs;

    @XmlElement(name = "isbn", type = Isbn.class)
    private List<Isbn> isbns;

    @XmlElement(name = "series", type = Series.class)
    private List<Series> series;

    @XmlElement(name = "school", type = School.class)
    private List<School> schools;

    @XmlElement(name = "chapter", type = Chapter.class)
    private List<Chapter> chapters;

    @XmlElement(name = "publnr", type = Publnr.class)
    private List<Publnr> publnrs;

}
