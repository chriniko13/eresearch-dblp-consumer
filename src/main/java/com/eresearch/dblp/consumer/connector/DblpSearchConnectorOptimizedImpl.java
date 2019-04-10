package com.eresearch.dblp.consumer.connector;

import com.codahale.metrics.Timer;
import com.eresearch.dblp.consumer.connector.context.DblpJAXBContextsHolder;
import com.eresearch.dblp.consumer.connector.guard.DblpAuthorsNoResultsAvailableGuard;
import com.eresearch.dblp.consumer.connector.registry.DblpSearchRegistry;
import com.eresearch.dblp.consumer.connector.worker.DblpAuthorFetcherWorker;
import com.eresearch.dblp.consumer.connector.worker.configuration.FetcherWorkersConfiguration;
import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthors;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.error.EresearchDblpConsumerError;
import com.eresearch.dblp.consumer.exception.BusinessProcessingException;
import com.eresearch.dblp.consumer.metrics.entries.ConnectorLayerMetricEntry;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Log4j
@Component
public class DblpSearchConnectorOptimizedImpl implements DblpSearchConnector {

    @Autowired
    @Qualifier("workerOperationsExecutor")
    private ExecutorService workerOperationsExecutor;

    @Autowired
    private ObjectFactory<DblpAuthorFetcherWorker> dblpAuthorFetcherWorkerObjectFactory;

    @Autowired
    private DblpSearchRegistry dblpSearchRegistry;

    @Autowired
    private FetcherWorkersConfiguration fetcherWorkersConfiguration;

    @Autowired
    private DblpJAXBContextsHolder dblpJAXBContextsHolder;

    @Autowired
    private ConnectorLayerMetricEntry connectorLayerMetricEntry;

    @Autowired
    private DblpAuthorsNoResultsAvailableGuard dblpAuthorsNoResultsAvailableGuard;

    @Autowired
    @Qualifier("basicRetryPolicyForOptimizedConnector")
    private RetryPolicy basicRetryPolicyForOptimizedConnector;

    @Override
    public Map<DblpAuthor, List<Dblp>> searchDblp(DblpConsumerDto dblpConsumerDto) throws BusinessProcessingException {

        Timer.Context timeContext = connectorLayerMetricEntry.getConnectorLayerTimer().time();
        try {

            return Failsafe
                    .with(basicRetryPolicyForOptimizedConnector)
                    .withFallback(() -> {
                        throw new BusinessProcessingException(
                                EresearchDblpConsumerError.BUSINESS_PROCESSING_ERROR,
                                EresearchDblpConsumerError.BUSINESS_PROCESSING_ERROR.getMessage());
                    })
                    .onSuccess(s -> log.info("DblpSearchConnectorOptimizedImpl#searchDblp, completed successfully!"))
                    .onFailure(error -> log.error("DblpSearchConnectorOptimizedImpl#searchDblp, failed!"))
                    .onAbort(error -> log.error("DblpSearchConnectorOptimizedImpl#searchDblp, aborted!"))
                    .get(context -> {

                        long startTime = context.getStartTime().toMillis();
                        long elapsedTime = context.getElapsedTime().toMillis();
                        int executions = context.getExecutions();

                        String message = String.format("DblpSearchConnectorOptimizedImpl#searchDblp, retrying...with params: " +
                                "[executions=%s, startTime(ms)=%s, elapsedTime(ms)=%s]", executions, startTime, elapsedTime);

                        log.warn(message);

                        return doExtraction(dblpConsumerDto);
                    });

        } finally {
            timeContext.stop();
        }
    }

    private Map<DblpAuthor, List<Dblp>> doExtraction(DblpConsumerDto dblpConsumerDto) throws JAXBException, MalformedURLException, InterruptedException, ExecutionException, BusinessProcessingException {
        String authorName = dblpSearchRegistry.constructAuthorName(dblpConsumerDto);

        //1st step - find author entry in dblp.
        Unmarshaller dblpAuthorsUnmarshaller = dblpJAXBContextsHolder
                .getDblpAuthorsJaxbContext()
                .createUnmarshaller();
        DblpAuthors dblpAuthors = dblpSearchRegistry.getDblpAuthors(dblpAuthorsUnmarshaller, authorName);

        if (dblpAuthorsNoResultsAvailableGuard.test(dblpAuthors)) return Collections.emptyMap();

        final ConcurrentHashMap<DblpAuthor, List<Dblp>> results = new ConcurrentHashMap<>(); //NOTE: high tune this in future.
        final List<List<DblpAuthor>> dblpAuthorsChunked
                = Lists.partition(dblpAuthors.getAuthors(),
                fetcherWorkersConfiguration.getNumberOfDblpAuthorFetcherWorkersToCreate());

        final List<Future<Boolean>> workersJobStatus = Collections.synchronizedList(new LinkedList<>());

        for (List<DblpAuthor> dblpAuthorsForWorker : dblpAuthorsChunked) {

            DblpAuthorFetcherWorker dblpAuthorFetcherWorker = dblpAuthorFetcherWorkerObjectFactory.getObject();
            dblpAuthorFetcherWorker.setDblpAuthorsToProcess(dblpAuthorsForWorker);
            dblpAuthorFetcherWorker.setResultsMap(results);

            workersJobStatus.add(workerOperationsExecutor.submit(dblpAuthorFetcherWorker));
        }

        //check if an error occurred during the processing.
        boolean successfulWorkerTaskAccumulator = true;
        for (Future<Boolean> workerJobStatus : workersJobStatus) {
            Boolean workerJobResult = workerJobStatus.get();
            successfulWorkerTaskAccumulator = successfulWorkerTaskAccumulator && workerJobResult;
        }

        //if we are here all workers finished (we call get to all worker-threads, see above).
        if (!successfulWorkerTaskAccumulator) {
            throw new BusinessProcessingException(
                    EresearchDblpConsumerError.BUSINESS_PROCESSING_ERROR,
                    EresearchDblpConsumerError.BUSINESS_PROCESSING_ERROR.getMessage());
        } else {
            return results;
        }
    }
}
