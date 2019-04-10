package com.eresearch.dblp.consumer.connector.delayer;

import lombok.extern.log4j.Log4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;


@Log4j
@Aspect
@Component
public class DblpCallsDelayer {

    @Value("${dblp.methods.time.delay.between.calls}")
    private String dblpMethodTimesDelayBetweenCallsStr;

    private int dblpMethodTimesDelayBetweenCalls;

    @PostConstruct
    public void init() {
        dblpMethodTimesDelayBetweenCalls = Integer.parseInt(dblpMethodTimesDelayBetweenCallsStr);
    }

    @Pointcut("execution (* com.eresearch.dblp.consumer.connector.registry.DblpSearchRegistry.getDblpAuthors(..))")
    private void getDblpAuthorsPointcut() {
    }

    @Pointcut("execution (* com.eresearch.dblp.consumer.connector.registry.DblpSearchRegistry.getDblpAuthorPublications(..))")
    private void getDblpAuthorPublicationsPointcut() {
    }

    @Pointcut("execution (* com.eresearch.dblp.consumer.connector.registry.DblpSearchRegistry.getDblpEntry(..))")
    private void getDblpEntryPointcut() {
    }

    @Before("getDblpAuthorsPointcut() " +
            "|| getDblpAuthorPublicationsPointcut()" +
            "|| getDblpEntryPointcut()")
    public void delayerBefore(JoinPoint joinPoint) {

        log.info("logBefore() is running!");
        log.info("hijacked : " + joinPoint.getSignature().getName());
        log.info("******");
        delayer();
    }

    private void delayer() {
        try {
            TimeUnit.SECONDS.sleep(dblpMethodTimesDelayBetweenCalls); //in order to not get HTTP-CODE-429 (too many requests) from dblp.
        } catch (InterruptedException e) {
            log.error("DblpCallsDelayer#delayerBefore --- error occurred", e);
        }
    }
}
