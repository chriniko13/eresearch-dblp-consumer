package com.eresearch.dblp.consumer.metrics.entries;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.eresearch.dblp.consumer.service.DblpConsumerService;

@Component
public class ServiceLayerMetricEntry {

    @Qualifier("appMetricRegistry")
    @Autowired
    private MetricRegistry metricRegistry;

    private Timer serviceLayerTimer;

    @PostConstruct
    public void init() {
        registerTimers();
    }

    private void registerTimers() {
        String timerName = MetricRegistry.name(DblpConsumerService.class, "dblpConsumerOperation", "timer");
        serviceLayerTimer = metricRegistry.timer(timerName);
    }

    public Timer getServiceLayerTimer() {
        return serviceLayerTimer;
    }
}
