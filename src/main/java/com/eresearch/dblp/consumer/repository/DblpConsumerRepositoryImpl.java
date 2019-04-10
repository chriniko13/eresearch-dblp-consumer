package com.eresearch.dblp.consumer.repository;

import com.codahale.metrics.Timer;
import com.eresearch.dblp.consumer.dao.DblpConsumerDao;
import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.DblpResultsDto;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.metrics.entries.RepositoryLayerMetricEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Log4j
@Repository
public class DblpConsumerRepositoryImpl implements DblpConsumerRepository {


    @Autowired
    private Clock clock;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("dbOperationsExecutor")
    private ExecutorService dbOperationsExecutor;

    @Autowired
    private RepositoryLayerMetricEntry repositoryLayerMetricEntry;

    @Autowired
    private DblpConsumerDao dblpConsumerDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Qualifier("transactionTemplate")
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void save(DblpConsumerDto dblpConsumerDto, DblpResultsDto dblpResultsDto) {

        Runnable task = saveTask(dblpConsumerDto, dblpResultsDto);
        dbOperationsExecutor.submit(task);

    }

    private Runnable saveTask(final DblpConsumerDto dblpConsumerDto, final DblpResultsDto dblpResultsDto) {
        return () -> {

            Timer.Context context = repositoryLayerMetricEntry.getRepositoryLayerTimer().time();
            try {

                final String sql = dblpConsumerDao.getInsertQueryForSearchResultsTable();

                final Map<DblpAuthor, List<Dblp>> resultsToStore = dblpResultsDto.getResults();

                final String authorName = objectMapper.writeValueAsString(dblpConsumerDto);

                final Timestamp creationTimestamp = Timestamp.from(Instant.now(clock));

                this.executeSaveStatements(sql, resultsToStore, authorName, creationTimestamp);

                log.info("DblpConsumerRepositoryImpl#save --- operation completed successfully.");

            } catch (JsonProcessingException e) {

                log.error("DblpConsumerRepositoryImpl#save --- error occurred --- not even tx initialized.", e);

            } finally {
                context.stop();
            }

        };
    }

    private void executeSaveStatements(final String sql,
                                       final Map<DblpAuthor, List<Dblp>> resultsToStore,
                                       final String authorName,
                                       final Timestamp creationTimestamp) {

        for (Map.Entry<DblpAuthor, List<Dblp>> resultToStore : resultsToStore.entrySet()) {
            executeSaveStatement(sql, authorName, creationTimestamp, resultToStore);
        }
    }

    private void executeSaveStatement(final String sql,
                                      final String authorName,
                                      final Timestamp creationTimestamp,
                                      final Map.Entry<DblpAuthor, List<Dblp>> resultToStore) {

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

                try {

                    DblpAuthor dblpAuthor = resultToStore.getKey();
                    String results = objectMapper.writeValueAsString(resultToStore.getValue());

                    jdbcTemplate.update(sql,
                            authorName,
                            dblpAuthor.toString(),
                            results,
                            creationTimestamp);

                } catch (DataAccessException | JsonProcessingException e) {
                    log.error("DblpConsumerRepositoryImpl#save --- error occurred --- proceeding with rollback.", e);
                    transactionStatus.setRollbackOnly();
                }
            }

        });
    }
}
