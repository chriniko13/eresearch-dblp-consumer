package com.eresearch.dblp.consumer.application.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import lombok.extern.log4j.Log4j;

/**
 * This listener listens for the event which is produced when the application is
 * ready.
 * 
 * @author chriniko
 *
 */
@Log4j
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		// Note: add functionality according to your needs...
		log.info("~~~Application is Ready~~~");
	}

}
