package com.randomvideos139.website;

import com.randomvideos139.website.service.DataSyncService;
import com.randomvideos139.website.repository.ChannelStatsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RandomVideos139WebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RandomVideos139WebsiteApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(DataSyncService dataSyncService, ChannelStatsRepository channelStatsRepository) {
        return args -> {
            if (channelStatsRepository.count() == 0) {
                // Only sync if database is empty
                dataSyncService.syncAllData();
            }
        };
    }
}
