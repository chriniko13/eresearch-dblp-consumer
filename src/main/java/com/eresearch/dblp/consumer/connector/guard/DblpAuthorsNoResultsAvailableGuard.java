package com.eresearch.dblp.consumer.connector.guard;

import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthors;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class DblpAuthorsNoResultsAvailableGuard implements Predicate<DblpAuthors> {

    @Override
    public boolean test(DblpAuthors dblpAuthors) {
        return noResultsAvailable(dblpAuthors);
    }

    private boolean noResultsAvailable(DblpAuthors dblpAuthors) {

        if (dblpAuthors == null
                || dblpAuthors.getAuthors() == null
                || dblpAuthors.getAuthors().isEmpty()) {
            log.info("DblpAuthorsNoResultsAvailableGuard#noResultsAvailable --- no results for provided info.");
            return true;
        }
        return false;
    }
}
