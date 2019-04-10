package com.eresearch.dblp.consumer.resource;

import com.codahale.metrics.Timer;
import com.eresearch.dblp.consumer.dto.DblpConsumerDto;
import com.eresearch.dblp.consumer.dto.DblpImmediateResultDto;
import com.eresearch.dblp.consumer.dto.DblpResultsDto;
import com.eresearch.dblp.consumer.exception.BusinessProcessingException;
import com.eresearch.dblp.consumer.exception.DataValidationException;
import com.eresearch.dblp.consumer.metrics.entries.ResourceLayerMetricEntry;
import com.eresearch.dblp.consumer.service.DblpConsumerService;
import com.eresearch.dblp.consumer.validator.Validator;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j
@RestController
@RequestMapping("/dblp-consumer")
public class DblpConsumerResourceImpl implements DblpConsumerResource {

    private static final Long DEFERRED_RESULT_TIMEOUT = TimeUnit.MILLISECONDS.toMinutes(18);

    @Qualifier("dblpConsumerExecutor")
    @Autowired
    private ExecutorService scopusConsumerExecutor;

    @Autowired
    private DblpConsumerService dblpConsumerService;

    @Autowired
    private Validator<DblpConsumerDto> dblpConsumerDtoValidator;

    @Autowired
    private ResourceLayerMetricEntry resourceLayerMetricEntry;

    @RequestMapping(method = RequestMethod.POST, path = "/find", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    DeferredResult<DblpResultsDto> dblpConsumerOperation(
            @RequestBody DblpConsumerDto dblpConsumerDto) throws BusinessProcessingException {

        DeferredResult<DblpResultsDto> deferredResult = new DeferredResult<>(DEFERRED_RESULT_TIMEOUT);

        Runnable task = dblpConsumerOperation(dblpConsumerDto, deferredResult);
        scopusConsumerExecutor.execute(task);

        return deferredResult;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/find-q", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<DblpImmediateResultDto> dblpNonBlockConsumption(
            @RequestHeader(value = "Transaction-Id") String transactionId,
            @RequestBody DblpConsumerDto dblpConsumerDto) throws DataValidationException {

        dblpConsumerDtoValidator.validate(dblpConsumerDto);

        Runnable task = () -> dblpConsumerService.dblpNonBlockConsumption(transactionId, dblpConsumerDto);
        scopusConsumerExecutor.execute(task);

        return ResponseEntity.ok(new DblpImmediateResultDto("Response will be written in queue."));
    }

    private Runnable dblpConsumerOperation(DblpConsumerDto dblpConsumerDto,
                                           DeferredResult<DblpResultsDto> deferredResult) {

        return () -> {

            final Timer.Context context = resourceLayerMetricEntry.getResourceLayerTimer().time();
            try {

                dblpConsumerDtoValidator.validate(dblpConsumerDto);
                DblpResultsDto dblpResultsDto = dblpConsumerService.dblpConsumerOperation(dblpConsumerDto);
                resourceLayerMetricEntry.getSuccessResourceLayerCounter().inc();
                deferredResult.setResult(dblpResultsDto);

            } catch (DataValidationException | BusinessProcessingException e) {

                log.error("DblpConsumerResourceImpl#dblpConsumerOperation --- error occurred.", e);
                resourceLayerMetricEntry.getFailureResourceLayerCounter().inc();
                deferredResult.setErrorResult(e);

            } finally {
                context.stop();
            }

        };
    }
}
