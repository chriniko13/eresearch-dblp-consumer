package com.eresearch.dblp.consumer;

import com.eresearch.dblp.consumer.application.listener.*;
import com.eresearch.dblp.consumer.db.DbOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * This is the entry point for our microservice.
 *
 * @author chriniko
 */

@SpringBootApplication
public class EresearchDblpConsumerApplication implements CommandLineRunner, ApplicationRunner {

    public static void main(String[] args) {

        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(
                EresearchDblpConsumerApplication.class);

        registerApplicationListeners(springApplicationBuilder);

        springApplicationBuilder
                .web(true)
                .run(args);

    }


    private static void registerApplicationListeners(final SpringApplicationBuilder springApplicationBuilder) {

        springApplicationBuilder.listeners(new ApplicationEnvironmentPreparedEventListener());
        springApplicationBuilder.listeners(new ApplicationFailedEventListener());
        springApplicationBuilder.listeners(new ApplicationReadyEventListener());
        springApplicationBuilder.listeners(new ApplicationStartedEventListener());
        springApplicationBuilder.listeners(new BaseApplicationEventListener());

    }

    @Autowired
    private DbOperations dbOperations;

    @Override
    public void run(String... args) throws Exception {
        dbOperations.runTask();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //NOTE: add your scenarios if needed.
    }
}
