package com.eresearch.dblp.consumer.connector.context;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.stereotype.Component;

import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthors;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpPerson;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class DblpJAXBContextsHolder {

    private JAXBContext dblpAuthorsJaxbContext;
    private JAXBContext dblpPersonJaxbContext;
    private JAXBContext dblpJaxbContext;

    @PostConstruct
    public void init() {

        try {

            dblpAuthorsJaxbContext = JAXBContext.newInstance(DblpAuthors.class);
            dblpPersonJaxbContext = JAXBContext.newInstance(DblpPerson.class);
            dblpJaxbContext = JAXBContext.newInstance(Dblp.class);

        } catch (JAXBException ex) {
            log.error(
                    "DblpJAXBContextsHolder#init --- error occurred.",
                    ex);
        }

    }

    public JAXBContext getDblpAuthorsJaxbContext() {
        return dblpAuthorsJaxbContext;
    }

    public JAXBContext getDblpPersonJaxbContext() {
        return dblpPersonJaxbContext;
    }

    public JAXBContext getDblpJaxbContext() {
        return dblpJaxbContext;
    }
}
