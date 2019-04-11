package com.eresearch.dblp.consumer.connector;

import com.codahale.metrics.Timer;
import com.eresearch.dblp.consumer.connector.context.DblpJAXBContextsHolder;
import com.eresearch.dblp.consumer.connector.guard.DblpAuthorsNoResultsAvailableGuard;
import com.eresearch.dblp.consumer.connector.guard.DblpPersonNoResultsAvailableGuard;
import com.eresearch.dblp.consumer.connector.registry.DblpSearchRegistry;
import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthors;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpKey;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpPerson;
import com.eresearch.dblp.consumer.error.EresearchDblpConsumerError;
import com.eresearch.dblp.consumer.exception.BusinessProcessingException;
import com.eresearch.dblp.consumer.metrics.entries.ConnectorLayerMetricEntry;
import lombok.extern.log4j.Log4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.CheckedConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.MalformedURLException;
import java.util.*;

@Log4j
@Component
public class DblpSearchConnectorImpl implements DblpSearchConnector {

    @Autowired
    private DblpSearchRegistry dblpSearchRegistry;

    @Autowired
    private DblpJAXBContextsHolder dblpJAXBContextsHolder;

    @Autowired
    private ConnectorLayerMetricEntry connectorLayerMetricEntry;

    @Autowired
    private DblpAuthorsNoResultsAvailableGuard dblpAuthorsNoResultsAvailableGuard;

    @Autowired
    private DblpPersonNoResultsAvailableGuard dblpPersonNoResultsAvailableGuard;

    @Autowired
    @Qualifier("basicRetryPolicy")
    private RetryPolicy basicRetryPolicy;

    @Override
    public Map<DblpAuthor, List<Dblp>> searchDblp(DblpConsumerDto dblpConsumerDto) throws BusinessProcessingException {

        Timer.Context timeContext = connectorLayerMetricEntry.getConnectorLayerTimer().time();
        try {

            return Failsafe
                    .with(basicRetryPolicy)
                    .withFallback((CheckedConsumer<? extends Throwable>) error -> {

                        log.error("dblp search connector error occurred, " + error.getMessage(), error);

                        throw new BusinessProcessingException(
                                EresearchDblpConsumerError.CONNECTOR_CONNECTION_ERROR,
                                EresearchDblpConsumerError.CONNECTOR_CONNECTION_ERROR.getMessage());
                    })
                    .onSuccess(s -> log.info("DblpSearchConnectorImpl#searchDblp, completed successfully!"))
                    .onFailure(error -> log.error("DblpSearchConnectorImpl#searchDblp, failed!"))
                    .onAbort(error -> log.error("DblpSearchConnectorImpl#searchDblp, aborted!"))
                    .get(context -> {

                        long startTime = context.getStartTime().toMillis();
                        long elapsedTime = context.getElapsedTime().toMillis();
                        int executions = context.getExecutions();

                        String message = String.format("DblpSearchConnectorImpl#searchDblp, retrying...with params: " +
                                "[executions=%s, startTime(ms)=%s, elapsedTime(ms)=%s]", executions, startTime, elapsedTime);

                        log.warn(message);

                        return doExtraction(dblpConsumerDto);
                    });

        } finally {
            timeContext.stop();
        }
    }

    private Map<DblpAuthor, List<Dblp>> doExtraction(DblpConsumerDto dblpConsumerDto) throws JAXBException, MalformedURLException {

        String authorName = dblpSearchRegistry.constructAuthorName(dblpConsumerDto);

        //1st step - find author entry in dblp.
        Unmarshaller dblpAuthorsUnmarshaller = dblpJAXBContextsHolder
                .getDblpAuthorsJaxbContext()
                .createUnmarshaller();
        DblpAuthors dblpAuthors = dblpSearchRegistry.getDblpAuthors(dblpAuthorsUnmarshaller, authorName);

        if (dblpAuthorsNoResultsAvailableGuard.test(dblpAuthors)) return Collections.emptyMap();

        Unmarshaller dblpPersonUnmarshaller = dblpJAXBContextsHolder
                .getDblpPersonJaxbContext()
                .createUnmarshaller();
        Unmarshaller dblpUnmarshaller = dblpJAXBContextsHolder
                .getDblpJaxbContext()
                .createUnmarshaller();

        return getDblpEntries(dblpAuthors, dblpPersonUnmarshaller, dblpUnmarshaller);
    }

    private Map<DblpAuthor, List<Dblp>> getDblpEntries(DblpAuthors dblpAuthors, Unmarshaller dblpPersonUnmarshaller, Unmarshaller dblpUnmarshaller)
            throws MalformedURLException, JAXBException {

        final Map<DblpAuthor, List<Dblp>> dblpResults = new LinkedHashMap<>();

        for (DblpAuthor dblpAuthor : dblpAuthors.getAuthors()) {

            //2nd step - for every retrieved author get his/her publications' entries.
            DblpPerson dblpPerson = dblpSearchRegistry.getDblpAuthorPublications(dblpPersonUnmarshaller, dblpAuthor);
            if (dblpPersonNoResultsAvailableGuard.test(dblpPerson)) {
                continue;
            }

            //3rd step - for every publication get the detailed info.
            List<Dblp> dblpEntries = new LinkedList<>();
            for (DblpKey dblpKey : dblpPerson.getDblpKeys()) {

                Dblp dblp = dblpSearchRegistry.getDblpEntry(dblpUnmarshaller, dblpKey);
                dblpEntries.add(dblp);
            }

            dblpResults.put(dblpAuthor, dblpEntries);
        }
        return dblpResults;
    }
}