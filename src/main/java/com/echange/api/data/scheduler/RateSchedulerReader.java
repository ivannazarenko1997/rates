package com.echange.api.data.scheduler;

import com.echange.api.data.service.CacheService;
import com.echange.api.data.service.impl.CacheServiceImpl;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateSchedulerReader {
    private static final Logger log = LoggerFactory.getLogger(RateSchedulerReader.class);
    private final CacheService cacheService;

    public RateSchedulerReader(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void refreshDataFromUrl() {
        log.info("Sheduler obtaining data currencies and rates from API" );
        cacheService.refreshDataFromUrl();
    }

    @PostConstruct
    public void initiazile(){
        log.info("Starting initialization rates and currencies");
        cacheService.refreshDataFromUrl();
    }
}
