package com.eresearch.dblp.consumer.application.actuator.health;

import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.DblpResultsDto;
import com.eresearch.dblp.consumer.exception.BusinessProcessingException;
import com.eresearch.dblp.consumer.service.DblpConsumerService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Log4j
@Component
public class EresearchDblpConsumerHealthCheck extends AbstractHealthIndicator {

    @Autowired
    private DblpConsumerService dblpConsumerService;

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

        //check jms (active mq) is up...
        new JmsHealthIndicator(jmsTemplate.getConnectionFactory());
    }

    private Optional<Exception> specificHealthCheck() {

        //check if required table(s) exist...

        //check if we can get a response from elsevier-api...
        if (Boolean.valueOf(doSpecificDblpApiHealthCheck)) {
            Optional<Exception> ex2 = specificDblpApiHealthCheck();
            if (ex2.isPresent()) {
                return ex2;
            }
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
