package com.eresearch.dblp.consumer.connector.registry;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthors;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpKey;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpPerson;

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

    private static final Function<String, String> DBLP_AUTHOR_SEARCH_URL_MAKER
            = authorName -> "https://dblp.uni-trier.de/search/author?xauthor=" + authorName;

    private static final Function<String, String> DBLP_AUTHOR_LOAD_PUBLICATIONS_URL_MAKER
            = urlpt -> "https://dblp.uni-trier.de/rec/pers/" + urlpt + "/xk";

    private static final Function<String, String> DBLP_ENTRY_FETCH_URL_MAKER
            = dblpKeyValue -> "https://dblp.uni-trier.de/rec/bibtex/" + dblpKeyValue + ".xml";

    private static final String DASH = " ";

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

        URL dblpAuthorSearchUrl = new URL(this.getDblpAuthorSearchUrl(authorName));

        return (DblpAuthors) dblpAuthorsUnmarshaller.unmarshal(dblpAuthorSearchUrl);
    }

    public DblpPerson getDblpAuthorPublications(Unmarshaller dblpPersonUnmarshaller, DblpAuthor dblpAuthor)
            throws MalformedURLException, JAXBException {

        String urlpt = dblpAuthor.getUrlpt();
        String dblpAuthorPublicationsUrl = this.getDblpAuthorLoadPublicationsUrl(urlpt);
        URL dblpAuthorPublicationsResource = new URL(dblpAuthorPublicationsUrl);

        return (DblpPerson) dblpPersonUnmarshaller.unmarshal(dblpAuthorPublicationsResource);
    }

    public Dblp getDblpEntry(Unmarshaller dblpUnmarshaller, DblpKey dblpKey)
            throws MalformedURLException, JAXBException {

        String dblpKeyValue = dblpKey.getValue();
        String dblpEntryUrl = this.getDblpEntryFetchUrl(dblpKeyValue);
        URL dblpEntryResource = new URL(dblpEntryUrl);

        return (Dblp) dblpUnmarshaller.unmarshal(dblpEntryResource);
    }

    private String getDblpAuthorSearchUrl(String authorName) {
        return DBLP_AUTHOR_SEARCH_URL_MAKER.apply(authorName);
    }

    private String getDblpAuthorLoadPublicationsUrl(String urlpt) {
        return DBLP_AUTHOR_LOAD_PUBLICATIONS_URL_MAKER.apply(urlpt);
    }

    private String getDblpEntryFetchUrl(String dblpKeyValue) {
        return DBLP_ENTRY_FETCH_URL_MAKER.apply(dblpKeyValue);
    }
}
