package com.eresearch.dblp.consumer.application.actuator.health;

import com.eresearch.dblp.consumer.dao.DblpConsumerDao;
import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.DblpResultsDto;
import com.eresearch.dblp.consumer.exception.BusinessProcessingException;
import com.eresearch.dblp.consumer.service.DblpConsumerService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Log4j
@Component
public class EresearchDblpConsumerHealthCheck extends AbstractHealthIndicator {

    @Qualifier("hikariDataSource")
    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private DblpConsumerService dblpConsumerService;

    @Autowired
    private DblpConsumerDao dblpConsumerDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${do.specific.dblp.api.health.check}")
    private String doSpecificDblpApiHealthCheck;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        this.performBasicHealthChecks();

        Optional<Exception> ex = this.specificHealthCheck();

        if (ex.isPresent()) {
            builder.down(ex.get());
        } else {
            builder.up();
        }
    }

    private void performBasicHealthChecks() {
        //check disk...
        DiskSpaceHealthIndicatorProperties diskSpaceHealthIndicatorProperties
                = new DiskSpaceHealthIndicatorProperties();
        diskSpaceHealthIndicatorProperties.setThreshold(10737418240L); /*10 GB*/
        new DiskSpaceHealthIndicator(diskSpaceHealthIndicatorProperties);

        //check datasource...
        new DataSourceHealthIndicator(hikariDataSource);

        //check jms (active mq) is up...
        new JmsHealthIndicator(jmsTemplate.getConnectionFactory());
    }

    private Optional<Exception> specificHealthCheck() {

        //check if required table(s) exist...
        Optional<Exception> ex1 = this.specificDbHealthCheck();
        if (ex1.isPresent()) {
            return ex1;
        }

        //check if we can get a response from elsevier-api...
        if (Boolean.valueOf(doSpecificDblpApiHealthCheck)) {
            Optional<Exception> ex2 = specificDblpApiHealthCheck();
            if (ex2.isPresent()) {
                return ex2;
            }
        }

        return Optional.empty();
    }

    private Optional<Exception> specificDbHealthCheck() {

        if (Objects.isNull(hikariDataSource)) {
            log.error("EresearchDblpConsumerHealthCheck#specificDbHealthCheck --- hikariDataSource is null.");
            return Optional.of(new NullPointerException("hikariDataSource is null."));
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(hikariDataSource);

        try {
            jdbcTemplate.execute(dblpConsumerDao.getSelectQueryForSearchResultsTable());
        } catch (DataAccessException ex) {
            log.error("EresearchDblpConsumerHealthCheck#specificDbHealthCheck --- db is in bad state.", ex);
            return Optional.of(ex);
        }

        return Optional.empty();
    }

    private Optional<Exception> specificDblpApiHealthCheck() {

        try {

            DblpResultsDto dblpResultsDto = dblpConsumerService.dblpConsumerOperation(DblpConsumerDto
                    .builder()
                    .firstname("Christos")
                    .surname("Skourlas")
                    .build());

            if (Objects.isNull(dblpResultsDto)) {
                log.error("EresearchDblpConsumerHealthCheck#specificDblpApiHealthCheck --- result from dblp-api is null.");
                return Optional.of(new NullPointerException("result from dblp-api is null."));
            }

        } catch (BusinessProcessingException ex) {

            log.error("EresearchDblpConsumerHealthCheck#specificDblpApiHealthCheck --- communication issue.", ex);
            return Optional.of(ex);

        }

        return Optional.empty();
    }

}
