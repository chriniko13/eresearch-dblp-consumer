package com.eresearch.dblp.consumer.metrics.entries;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.eresearch.dblp.consumer.connector.DblpSearchConnector;

@Component
public class ConnectorLayerMetricEntry {

    @Qualifier("appMetricRegistry")
    @Autowired
    private MetricRegistry metricRegistry;

    private Timer connectorLayerTimer;

    @PostConstruct
    public void init() {
        registerTimers();
    }

    private void registerTimers() {
        String timerName = MetricRegistry.name(DblpSearchConnector.class, "searchDblp", "timer");
        connectorLayerTimer = metricRegistry.timer(timerName);
    }

    public Timer getConnectorLayerTimer() {
        return connectorLayerTimer;
    }

}
