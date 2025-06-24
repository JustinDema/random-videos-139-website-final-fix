package com.randomvideos139.website.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ScheduledTaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private DataSyncService dataSyncService;
    
    /**
     * Scheduled task to sync all data every 12 hours
     * Runs at 6 AM and 6 PM every day
     */
    @Scheduled(cron = "0 0 6,18 * * *")
    public void syncDataEvery12Hours() {
        logger.info("Starting scheduled 12-hour data sync at {}", LocalDateTime.now().format(FORMATTER));
        
        try {
            dataSyncService.syncAllData();
            logger.info("Scheduled 12-hour data sync completed successfully at {}", 
                LocalDateTime.now().format(FORMATTER));
                
        } catch (Exception e) {
            logger.error("Error during scheduled 12-hour data sync: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Initial data sync on application startup
     * This ensures we have data available immediately
     */
    @Scheduled(initialDelay = 30000, fixedDelay = Long.MAX_VALUE)
    public void initialDataSync() {
        logger.info("Starting initial data sync on application startup at {}", 
            LocalDateTime.now().format(FORMATTER));
        
        try {
            // Check if we have any data
            if (!dataSyncService.getChannelStats().isPresent()) {
                logger.info("No existing data found, performing full initial sync...");
                dataSyncService.syncAllData();
            } else {
                logger.info("Existing data found, performing quick sync...");
                // Only sync channel stats if data exists, full sync will run on schedule
                dataSyncService.syncChannelStats();
            }
            
            logger.info("Initial data sync completed successfully at {}", 
                LocalDateTime.now().format(FORMATTER));
                
        } catch (Exception e) {
            logger.error("Error during initial data sync: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Health check task - runs every 30 minutes to verify API connectivity
     */
    @Scheduled(cron = "0 */30 * * * *")
    public void healthCheck() {
        try {
            boolean isConnected = dataSyncService.isApiConnected();
            if (isConnected) {
                logger.debug("YouTube API health check passed at {}", LocalDateTime.now().format(FORMATTER));
            } else {
                logger.warn("YouTube API health check failed at {}", LocalDateTime.now().format(FORMATTER));
            }
        } catch (Exception e) {
            logger.error("Error during YouTube API health check: {}", e.getMessage());
        }
    }
    
    /**
     * Manual trigger for full data sync (can be called via admin interface)
     */
    public void triggerFullSync() {
        logger.info("Manual full data sync triggered at {}", LocalDateTime.now().format(FORMATTER));
        
        try {
            dataSyncService.syncAllData();
            logger.info("Manual full data sync completed successfully at {}", 
                LocalDateTime.now().format(FORMATTER));
                
        } catch (Exception e) {
            logger.error("Error during manual full data sync: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete manual sync", e);
        }
    }
    
    /**
     * Get sync status information
     */
    public String getSyncStatus() {
        try {
            boolean apiConnected = dataSyncService.isApiConnected();
            boolean hasChannelData = dataSyncService.getChannelStats().isPresent();
            long videosCount = dataSyncService.getVideoRepository().count(); // Use count for total
            long playlistsCount = dataSyncService.getPlaylistRepository().count(); // Use count for total
            
            return String.format(
                "API Connected: %s | Channel Data: %s | Videos: %d | Playlists: %d | Last Check: %s",
                apiConnected ? "✓" : "✗",
                hasChannelData ? "✓" : "✗",
                videosCount,
                playlistsCount,
                LocalDateTime.now().format(FORMATTER)
            );
            
        } catch (Exception e) {
            return "Error checking sync status: " + e.getMessage();
        }
    }
}


