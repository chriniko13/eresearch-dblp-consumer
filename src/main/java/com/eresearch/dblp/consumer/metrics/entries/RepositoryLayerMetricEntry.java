package com.eresearch.dblp.consumer.metrics.entries;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.eresearch.dblp.consumer.repository.DblpConsumerRepository;

@Component
public class RepositoryLayerMetricEntry {

    @Qualifier("appMetricRegistry")
    @Autowired
    private MetricRegistry metricRegistry;

    private Timer repositoryLayerTimer;

    @PostConstruct
    public void init() {
        registerTimers();
    }

    private void registerTimers() {
        String timerName = MetricRegistry.name(DblpConsumerRepository.class, "save", "timer");
        repositoryLayerTimer = metricRegistry.timer(timerName);
    }

    public Timer getRepositoryLayerTimer() {
        return repositoryLayerTimer;
    }
}
