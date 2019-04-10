package com.eresearch.dblp.consumer.connector.worker;


import com.eresearch.dblp.consumer.connector.context.DblpJAXBContextsHolder;
import com.eresearch.dblp.consumer.connector.guard.DblpPersonNoResultsAvailableGuard;
import com.eresearch.dblp.consumer.connector.registry.DblpSearchRegistry;
import com.eresearch.dblp.consumer.connector.worker.configuration.FetcherWorkersConfiguration;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpKey;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpPerson;
import com.google.common.collect.Lists;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Log4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DblpAuthorFetcherWorker implements Callable<Boolean> {

    private
    @Setter
    List<DblpAuthor> dblpAuthorsToProcess;

    private
    @Setter
    ConcurrentHashMap<DblpAuthor, List<Dblp>> resultsMap;

    @Autowired
    private DblpSearchRegistry dblpSearchRegistry;

    @Autowired
    private ObjectFactory<DblpEntryFetcherWorker> dblpEntryFetcherWorkerObjectFactory;

    @Autowired
    @Qualifier("workerOperationsExecutor")
    private ExecutorService workerOperationsExecutor;

    @Autowired
    private FetcherWorkersConfiguration fetcherWorkersConfiguration;

    @Autowired
    private DblpJAXBContextsHolder dblpJAXBContextsHolder;

    @Autowired
    private DblpPersonNoResultsAvailableGuard dblpPersonNoResultsAvailableGuard;

    private ThreadLocal<Unmarshaller> dblpPersonUnmarshallerThreadLocal;

    @PostConstruct
    public void init() {
        try {

            Unmarshaller dblpPersonUnmarshaller = dblpJAXBContextsHolder
                    .getDblpPersonJaxbContext()
                    .createUnmarshaller();
            dblpPersonUnmarshallerThreadLocal = ThreadLocal.withInitial(() -> dblpPersonUnmarshaller);

        } catch (JAXBException ex) {
            String currentThreadName = Thread.currentThread().getName();

            log.error("DblpAuthorFetcherWorker[threadName=" + currentThreadName + "]#init --- error occurred.", ex);
        }
    }

    @Override
    public Boolean call() throws Exception {

        try {

            Boolean finalSuccessfulWorkerTaskAccumulator = true;

            for (DblpAuthor dblpAuthor : dblpAuthorsToProcess) {

                DblpPerson dblpPerson = dblpSearchRegistry.getDblpAuthorPublications(dblpPersonUnmarshallerThreadLocal.get(), dblpAuthor);

                if (dblpPersonNoResultsAvailableGuard.test(dblpPerson)) {
                    continue;
                }

                List<DblpKey> dblpKeys = dblpPerson.getDblpKeys();

                final List<DblpEntryFetcherWorker> dblpEntryFetcherWorkers = new LinkedList<>();
                final List<List<DblpKey>> dblpKeysChunked
                        = Lists.partition(dblpKeys,
                        fetcherWorkersConfiguration.getNumberOfDblpEntryFetcherWorkersToCreate());
                final List<Future<Boolean>> workersJobStatus = new LinkedList<>();

                for (List<DblpKey> dblpKeysForWorker : dblpKeysChunked) {

                    DblpEntryFetcherWorker dblpEntryFetcherWorker = dblpEntryFetcherWorkerObjectFactory.getObject();
                    dblpEntryFetcherWorker.setDblpKeysToProcess(dblpKeysForWorker);

                    dblpEntryFetcherWorkers.add(dblpEntryFetcherWorker);

                    workersJobStatus.add(workerOperationsExecutor.submit(dblpEntryFetcherWorker));
                }

                //check if an error occurred during the processing.
                boolean successfulWorkerTaskAccumulator = true;
                for (Future<Boolean> workerJobStatus : workersJobStatus) {
                    Boolean workerJobResult = workerJobStatus.get();
                    successfulWorkerTaskAccumulator = successfulWorkerTaskAccumulator && workerJobResult;
                }

                //if we are here all workers finished (we call get to all worker-threads, see above).

                if (!successfulWorkerTaskAccumulator) {
                    finalSuccessfulWorkerTaskAccumulator = false;
                    break; //stop working because at least one worker failed!
                } else {

                    //collect the results from workers.
                    List<Dblp> dblpEntries = dblpEntryFetcherWorkers
                            .stream()
                            .map(DblpEntryFetcherWorker::getDblpEntries)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());

                    dblpEntryFetcherWorkers.clear(); //clear the results list for the next author.
                    resultsMap.put(dblpAuthor, dblpEntries);
                }
            }
            return finalSuccessfulWorkerTaskAccumulator;

        } catch (JAXBException | MalformedURLException | InterruptedException | ExecutionException ex) {

            String currentThreadName = Thread.currentThread().getName();
            Throwable error = ex instanceof ExecutionException ? ex.getCause() : ex;

            log.error("DblpAuthorFetcherWorker[threadName=" + currentThreadName + "]#run --- error occurred.", error);
            return false; //worker finished with failure.
        }
    }
}
