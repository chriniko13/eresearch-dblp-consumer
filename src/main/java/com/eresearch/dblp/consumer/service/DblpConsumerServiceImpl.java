package com.eresearch.dblp.consumer.service;

import com.codahale.metrics.Timer;
import com.eresearch.dblp.consumer.application.configuration.JmsConfiguration;
import com.eresearch.dblp.consumer.connector.DblpSearchConnector;
import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.DblpQueueResultDto;
import com.eresearch.dblp.consumer.dto.DblpResultsDto;
import com.eresearch.dblp.consumer.dto.dblp.author.DblpAuthor;
import com.eresearch.dblp.consumer.dto.dblp.entry.generated.Dblp;
import com.eresearch.dblp.consumer.exception.BusinessProcessingException;
import com.eresearch.dblp.consumer.metrics.entries.ServiceLayerMetricEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Log4j
@Service
public class DblpConsumerServiceImpl implements DblpConsumerService {

    @Value("${dblp.consumer.multithread.approach}")
    private String dblpConsumerMultithreadApproach;

    @Autowired
    private Map<String, DblpSearchConnector> dblpSearchConnectorStrategies;

    @Autowired
    private Clock clock;

    @Autowired
    private ServiceLayerMetricEntry serviceLayerMetricEntry;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public DblpResultsDto dblpConsumerOperation(DblpConsumerDto dblpConsumerDto) throws BusinessProcessingException {

        Timer.Context context = serviceLayerMetricEntry.getServiceLayerTimer().time();
        try {

            DblpSearchConnector dblpSearchConnector = chooseDblpConsumptionApproach();

            DblpResultsDto result = new DblpResultsDto();

            Map<DblpAuthor, List<Dblp>> dblpResults = dblpSearchConnector.searchDblp(dblpConsumerDto);

            result.setDblpConsumerDto(dblpConsumerDto);
            result.setResultsSize(dblpResults.size());
            result.setResults(dblpResults);
            result.setOperationResult(Boolean.TRUE);
            result.setProcessFinishedDate(Instant.now(clock));

            return result;

        } catch (BusinessProcessingException ex) {
            log.error("DblpConsumerServiceImpl#dblpConsumerOperation --- error occurred.", ex);
            throw ex;
        } finally {
            context.stop();
        }
    }

    public void dblpNonBlockConsumption(String transactionId, DblpConsumerDto dblpConsumerDto) {

        DblpQueueResultDto dblpQueueResultDto = null;
        try {

            DblpResultsDto dblpResultsDto
                    = this.dblpConsumerOperation(dblpConsumerDto);

            dblpQueueResultDto = new DblpQueueResultDto(transactionId, null, dblpResultsDto);

        } catch (BusinessProcessingException e) {

            log.error("DblpConsumerServiceImpl#dblpNonBlockConsumption --- error occurred.", e);

            DblpResultsDto dblpResultsDto = new DblpResultsDto();
            dblpResultsDto.setOperationResult(false);

            dblpQueueResultDto = new DblpQueueResultDto(transactionId, e.toString(), dblpResultsDto);

        } finally {

            try {
                String resultForQueue = objectMapper.writeValueAsString(dblpQueueResultDto);
                jmsTemplate.convertAndSend(JmsConfiguration.QUEUES.DBLP_RESULTS_QUEUE, resultForQueue);
            } catch (JsonProcessingException e) {
                //we can't do much things for the moment here...
                log.error("DblpConsumerServiceImpl#dblpNonBlockConsumption --- error occurred.", e);
            }

        }

    }

    private DblpSearchConnector chooseDblpConsumptionApproach() {

        return Boolean.valueOf(dblpConsumerMultithreadApproach) ?
                dblpSearchConnectorStrategies.get("dblpSearchConnectorOptimizedImpl")
                : dblpSearchConnectorStrategies.get("dblpSearchConnectorImpl");

    }
}
