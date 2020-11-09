package com.glodon.pcop.cimsvc.config.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent> {
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartingEventListener.class);

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {

        System.out.println("ApplicationStartingEvent事件发布:" + event.getTimestamp());
        log.info("ApplicationStartingEvent事件发布:{}", event.getTimestamp());
        log.info("ApplicationStartingEvent事件发布:{}", event);
    }

}