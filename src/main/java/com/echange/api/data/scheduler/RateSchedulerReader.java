package com.echange.api.data.scheduler;

import com.echange.api.data.service.CacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateSchedulerReader {
    private final CacheService cacheService;

    public RateSchedulerReader(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void refreshDataFromUrl() {
        cacheService.refreshDataFromUrl();
    }

    @PostConstruct
    public void initiazile(){
        cacheService.refreshDataFromUrl();
    }
}
