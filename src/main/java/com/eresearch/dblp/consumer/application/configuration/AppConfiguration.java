package com.eresearch.dblp.consumer.application.configuration;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.eresearch.dblp.consumer.deserializer.InstantDeserializer;
import com.eresearch.dblp.consumer.serializer.InstantSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.concurrent.*;

@EnableScheduling
@EnableAspectJAutoProxy


@Configuration
public class AppConfiguration implements SchedulingConfigurer {

    @Bean
    @Qualifier("basicRetryPolicy")
    public RetryPolicy basicRetryPolicy() {
        return new RetryPolicy()
                .retryOn(Arrays.asList(JAXBException.class, MalformedURLException.class, IOException.class))
                .withMaxRetries(15)
                .withDelay(6, TimeUnit.MINUTES)
                .withJitter(30, TimeUnit.SECONDS);
    }

    @Bean
    @Qualifier("basicRetryPolicyForOptimizedConnector")
    public RetryPolicy basicRetryPolicyForOptimizedConnector() {
        return new RetryPolicy()
                .retryOn(Arrays.asList(JAXBException.class, MalformedURLException.class, IOException.class, InterruptedException.class, ExecutionException.class))
                .withMaxRetries(15)
                .withDelay(6, TimeUnit.MINUTES)
                .withJitter(30, TimeUnit.SECONDS);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, new InstantSerializer());
        javaTimeModule.addDeserializer(Instant.class, new InstantDeserializer());
        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }

    /*
     * Handling (front) asynchronous communications.
     */
    @Bean(destroyMethod = "shutdownNow")
    @Qualifier("dblpConsumerExecutor")
    public ExecutorService dblpConsumerExecutor() {
        return new ThreadPoolExecutor(
                20, 120,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("dblp-consumer-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /*
     * Handling db operations.
     */
    @Bean(destroyMethod = "shutdownNow")
    @Qualifier("dbOperationsExecutor")
    public ExecutorService dbOperationsExecutor() {
        return new ThreadPoolExecutor(
                20, 120,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("db-operations-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /*
     * Handling worker (threads) operations.
     */
    @Bean(destroyMethod = "shutdownNow")
    @Qualifier("workerOperationsExecutor")
    public ExecutorService workerOperationsExecutor() {
        return new ThreadPoolExecutor(
                20, 120,
                5000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(300, true),
                new ThreadFactoryBuilder().setNameFormat("worker-operations-thread-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Value("${service.zone.id}")
    private ZoneId zoneId;

    @Bean
    public Clock clock() {
        return Clock.system(zoneId);
    }

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Bean(destroyMethod = "close")
    @Qualifier("hikariDataSource")
    public HikariDataSource hikariDataSource() {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(dbUrl);
        config.setUsername(username);
        config.setPassword(password);

        config.setMetricRegistry(this.metricRegistry());
        config.setHealthCheckRegistry(this.healthCheckRegistry());

        return new HikariDataSource(config);
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(this.hikariDataSource());
    }

    @Qualifier("transactionTemplate")
    @Bean
    public TransactionTemplate transactionTemplate() {

        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager());

        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        return transactionTemplate;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(this.hikariDataSource());
    }

    @Bean
    @Qualifier("appMetricRegistry")
    public MetricRegistry metricRegistry() {
        return new MetricRegistry();
    }


    @Bean
    public HealthCheckRegistry healthCheckRegistry() {
        return new HealthCheckRegistry();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(Executors.newSingleThreadScheduledExecutor());
    }
}
