package com.eresearch.dblp.consumer.connector.worker;

import com.eresearch.dblp.consumer.connector.context.DblpJAXBContextsHolder;
import com.eresearch.dblp.consumer.connector.registry.DblpSearchRegistry;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.dto.dblp.publication.DblpKey;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

@Log4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DblpEntryFetcherWorker implements Callable<Boolean> {

    private
    @Getter
    List<Dblp> dblpEntries;

    private
    @Setter
    List<DblpKey> dblpKeysToProcess;

    @Autowired
    private DblpSearchRegistry dblpSearchRegistry;

    @Autowired
    private DblpJAXBContextsHolder dblpJAXBContextsHolder;

    private ThreadLocal<Unmarshaller> dblpUnmarshallerThreadLocal;

    @PostConstruct
    public void init() {
        try {

            Unmarshaller dblpUnmarshaller = dblpJAXBContextsHolder
                    .getDblpJaxbContext()
                    .createUnmarshaller();
            dblpUnmarshallerThreadLocal = ThreadLocal.withInitial(() -> dblpUnmarshaller);

        } catch (JAXBException ex) {
            String currentThreadName = Thread.currentThread().getName();

            log.error("DblpEntryFetcherWorker[threadName=" + currentThreadName + "]#init --- error occurred.", ex);
        }
    }

    @Override
    public Boolean call() throws Exception {
        try {

            dblpEntries = new LinkedList<>();

            for (DblpKey dblpKey : dblpKeysToProcess) {

                Dblp dblpEntry = dblpSearchRegistry.getDblpEntry(dblpUnmarshallerThreadLocal.get(), dblpKey);
                dblpEntries.add(dblpEntry);
            }

            return true; //worker finished successfully.

        } catch (MalformedURLException | JAXBException ex) {

            String currentThreadName = Thread.currentThread().getName();

            log.error("DblpEntryFetcherWorker[threadName=" + currentThreadName + "]#run --- error occurred.", ex);

            return false; //worker finished with failure.
        }
    }
}
