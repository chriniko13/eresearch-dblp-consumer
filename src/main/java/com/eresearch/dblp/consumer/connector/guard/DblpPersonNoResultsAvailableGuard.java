package com.eresearch.dblp.consumer.connector.guard;

import com.eresearch.dblp.consumer.dto.dblp.publication.DblpPerson;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@Log4j
public class DblpPersonNoResultsAvailableGuard implements Predicate<DblpPerson> {

    @Override
    public boolean test(DblpPerson dblpPerson) {
        return noResultsAvailable(dblpPerson);
    }

    private boolean noResultsAvailable(DblpPerson dblpPerson) {

        if (dblpPerson == null
                || dblpPerson.getDblpKeys() == null
                || dblpPerson.getDblpKeys().isEmpty()) {
            log.info("DblpPersonNoResultsAvailableGuard#noResultsAvailable --- no results for provided info.");
            return true;
        }
        return false;

    }
}
