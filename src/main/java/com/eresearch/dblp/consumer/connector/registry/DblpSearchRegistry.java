package com.eresearch.dblp.consumer.connector.registry;

import com.eresearch.dblp.consumer.connector.context.DblpJAXBContextsHolder;
import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthors;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpKey;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpPerson;
import com.eresearch.dblp.consumer.service.CaptureDblpResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/*
 NOTE:

 Sequence usage of below functions: [DBLP_AUTHOR_SEARCH_URL_MAKER]
                                    -> [DBLP_AUTHOR_LOAD_PUBLICATIONS_URL_MAKER]
                                    -> [DBLP_ENTRY_FETCH_URL_MAKER]


 Sequence example usage of DBLP urls we hit:
 1) http://dblp.uni-trier.de/search/author?xauthor=Christos%20Skour
 2) http://dblp.uni-trier.de/rec/pers/s/Skourlas:Christos/xk
 3) http://dblp.uni-trier.de/rec/bibtex/journals/jcs/BelsisFGS08.xml

 */
@Component
public class DblpSearchRegistry {

    private static final String DASH = " ";

    @Value("${dblp-search-registry.capture-dblp-reponse}")
    private boolean captureDblpResponse;

    @Value("${dblp-author-search.url-placeholder}")
    private String dblpAuthorSearchUrlPlaceholder;

    @Value("${dblp-author-load-publications.url-placeholder}")
    private String dblpAuthorLoadPublicationsUrlPlaceholder;

    @Value("${dblp-entry-fetch.url-placeholder}")
    private String dblpEntryFetchUrlPlaceholder;

    @Autowired
    private CaptureDblpResponseService captureDblpResponseService;

    @Autowired
    private DblpJAXBContextsHolder dblpJAXBContextsHolder;

    /*
        Note: authorName = firstname + initials + surname.
     */
    public String constructAuthorName(DblpConsumerDto dblpConsumerDto) {
        return dblpConsumerDto.getFirstname() +
                Optional
                        .ofNullable(dblpConsumerDto.getInitials())
                        .filter(initials -> !initials.isEmpty())
                        .map(initials -> DASH + initials + DASH)
                        .orElse(DASH) +
                dblpConsumerDto.getSurname();
    }

    public DblpAuthors getDblpAuthors(Unmarshaller dblpAuthorsUnmarshaller, String authorName)
            throws MalformedURLException, JAXBException {

        URL dblpAuthorSearchUrl = new URL(dblpAuthorSearchUrlPlaceholder.replace("__PLACEHOLDER__", authorName));
        DblpAuthors result = (DblpAuthors) dblpAuthorsUnmarshaller.unmarshal(dblpAuthorSearchUrl);

        if (captureDblpResponse) {
            Marshaller marshaller = dblpJAXBContextsHolder.getDblpAuthorsJaxbContext().createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(result, stringWriter);

            captureDblpResponseService.log(
                    "getDblpAuthors_" + authorName,
                    stringWriter.toString(),
                    "xml"
            );
        }
        return result;
    }

    public DblpPerson getDblpAuthorPublications(Unmarshaller dblpPersonUnmarshaller, DblpAuthor dblpAuthor)
            throws MalformedURLException, JAXBException {

        String urlpt = dblpAuthor.getUrlpt();
        String dblpAuthorPublicationsUrl = dblpAuthorLoadPublicationsUrlPlaceholder.replace("__PLACEHOLDER__", urlpt);

        URL dblpAuthorPublicationsResource = new URL(dblpAuthorPublicationsUrl);
        DblpPerson result = (DblpPerson) dblpPersonUnmarshaller.unmarshal(dblpAuthorPublicationsResource);

        if (captureDblpResponse) {
            Marshaller marshaller = dblpJAXBContextsHolder.getDblpPersonJaxbContext().createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(result, stringWriter);

            captureDblpResponseService.log(
                    "getDblpAuthorPublications_" + urlpt.replaceAll("/", "\\$"),
                    stringWriter.toString(),
                    "xml"
            );
        }
        return result;
    }

    public Dblp getDblpEntry(Unmarshaller dblpUnmarshaller, DblpKey dblpKey)
            throws MalformedURLException, JAXBException {

        String dblpKeyValue = dblpKey.getValue();
        String dblpEntryUrl = dblpEntryFetchUrlPlaceholder.replace("__PLACEHOLDER__", dblpKeyValue);

        URL dblpEntryResource = new URL(dblpEntryUrl);
        Dblp result = (Dblp) dblpUnmarshaller.unmarshal(dblpEntryResource);

        if (captureDblpResponse) {
            Marshaller marshaller = dblpJAXBContextsHolder.getDblpJaxbContext().createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(result, stringWriter);

            captureDblpResponseService.log(
                    "getDblpEntry_" + dblpKeyValue.replaceAll("/", "\\$"),
                    stringWriter.toString(),
                    "xml"
            );
        }
        return result;
    }

}
