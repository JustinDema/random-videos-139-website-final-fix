package com.randomvideos139.website.service;

import com.randomvideos139.website.dto.YouTubeChannelResponse;
import com.randomvideos139.website.dto.YouTubeVideoResponse;
import com.randomvideos139.website.dto.YouTubePlaylistResponse;
import com.randomvideos139.website.entity.ChannelStats;
import com.randomvideos139.website.entity.Video;
import com.randomvideos139.website.entity.Playlist;
import com.randomvideos139.website.entity.UpdateHistory;
import com.randomvideos139.website.repository.ChannelStatsRepository;
import com.randomvideos139.website.repository.VideoRepository;
import com.randomvideos139.website.repository.PlaylistRepository;
import com.randomvideos139.website.repository.UpdateHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@Transactional
public class DataSyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSyncService.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    @Autowired
    private YouTubeApiService youTubeApiService;
    
    @Autowired
    private ChannelStatsRepository channelStatsRepository;
    
    @Autowired
    private VideoRepository videoRepository;
    
    @Autowired
    private PlaylistRepository playlistRepository;
    
    @Autowired
    private UpdateHistoryRepository updateHistoryRepository;
    
    @Value("${youtube.channel.id}")
    private String channelId;
    
    @Value("${youtube.channel.handle}")
    private String channelHandle;
    
    /**
     * Synchronize all data from YouTube API
     */
    public void syncAllData() {
        logger.info("Starting full data synchronization...");
        
        UpdateHistory updateHistory = new UpdateHistory("ALL");
        updateHistoryRepository.save(updateHistory);
        
        long startTime = System.currentTimeMillis();
        int totalRecordsUpdated = 0;
        
        try {
            // Sync channel statistics
            syncChannelStats();
            totalRecordsUpdated += 1;
            
            // Sync all videos with pagination and three-way comparison
            totalRecordsUpdated += syncVideos();
            
            // Sync playlists with three-way comparison
            totalRecordsUpdated += syncPlaylists();
            
            long durationSeconds = (System.currentTimeMillis() - startTime) / 1000;
            updateHistory.markAsSuccess(totalRecordsUpdated, durationSeconds);
            updateHistoryRepository.save(updateHistory);
            
            logger.info("Full data synchronization completed successfully. Updated {} records in {} seconds", 
                totalRecordsUpdated, durationSeconds);
            
        } catch (Exception e) {
            long durationSeconds = (System.currentTimeMillis() - startTime) / 1000;
            updateHistory.markAsFailed(e.getMessage(), durationSeconds);
            updateHistoryRepository.save(updateHistory);
            
            logger.error("Error during data synchronization: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Sync channel statistics
     */
    public void syncChannelStats() {
        logger.info("Syncing channel statistics...");
        
        try {
            YouTubeChannelResponse response = youTubeApiService.getChannelInfo();
            
            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                YouTubeChannelResponse.ChannelItem channelItem = response.getItems().get(0);
                
                // Find existing or create new
                Optional<ChannelStats> existingStats = channelStatsRepository.findByChannelId(channelItem.getId());
                ChannelStats channelStats = existingStats.orElse(new ChannelStats());
                
                // Update with new data
                updateChannelStatsFromApi(channelStats, channelItem);
                
                channelStatsRepository.save(channelStats);
                logger.info("Channel statistics updated successfully");
                
            } else {
                logger.warn("No channel data received from YouTube API");
            }
            
        } catch (Exception e) {
            logger.error("Error syncing channel statistics: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Sync all videos from the channel with pagination and three-way comparison
     */
    public int syncVideos() {
        logger.info("Starting video synchronization...");
        int totalVideosProcessed = 0;

        // Step 1: Fetch ALL Video IDs from YouTube
        List<YouTubeVideoResponse.VideoItem> youtubeVideoItems = youTubeApiService.getAllVideos();
        Set<String> youtubeVideoIds = youtubeVideoItems.stream()
                .map(YouTubeVideoResponse.VideoItem::getVideoId)
                .collect(Collectors.toSet());
        logger.info("Fetched {} video IDs from YouTube.", youtubeVideoIds.size());

        // Step 2: Fetch ALL Video IDs from the Database
        List<Video> dbVideos = videoRepository.findAll();
        Set<String> dbVideoIds = dbVideos.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toSet());
        logger.info("Fetched {} video IDs from the database.", dbVideoIds.size());

        // Step 3: Perform a Three-Way Comparison and Reconciliation

        // INSERT (New Videos)
        Set<String> newVideoIds = new HashSet<>(youtubeVideoIds);
        newVideoIds.removeAll(dbVideoIds);
        logger.info("Found {} new videos to insert.", newVideoIds.size());
        for (String videoId : newVideoIds) {
            try {
                // Find the corresponding video item from the fetched list
                Optional<YouTubeVideoResponse.VideoItem> videoItemOpt = youtubeVideoItems.stream()
                        .filter(item -> videoId.equals(item.getVideoId()))
                        .findFirst();

                if (videoItemOpt.isPresent()) {
                    Video video = new Video();
                    updateVideoFromApi(video, videoItemOpt.get());
                    videoRepository.save(video);
                    totalVideosProcessed++;
                }
            } catch (Exception e) {
                logger.error("Error inserting new video {}: {}", videoId, e.getMessage(), e);
            }
        }

        // UPDATE (Existing Videos)
        Set<String> commonVideoIds = new HashSet<>(youtubeVideoIds);
        commonVideoIds.retainAll(dbVideoIds);
        logger.info("Found {} common videos to update.", commonVideoIds.size());
        for (String videoId : commonVideoIds) {
            try {
                // Find the corresponding video item from the fetched list
                Optional<YouTubeVideoResponse.VideoItem> videoItemOpt = youtubeVideoItems.stream()
                        .filter(item -> videoId.equals(item.getVideoId()))
                        .findFirst();

                if (videoItemOpt.isPresent()) {
                    Optional<Video> existingVideo = videoRepository.findById(videoId);
                    existingVideo.ifPresent(video -> {
                        updateVideoFromApi(video, videoItemOpt.get());
                        videoRepository.save(video);
                    });
                    totalVideosProcessed++;
                }
            } catch (Exception e) {
                logger.error("Error updating video {}: {}", videoId, e.getMessage(), e);
            }
        }

        // DELETE (Removed Videos)
        Set<String> removedVideoIds = new HashSet<>(dbVideoIds);
        removedVideoIds.removeAll(youtubeVideoIds);
        logger.info("Found {} videos to delete.", removedVideoIds.size());
        for (String videoId : removedVideoIds) {
            try {
                videoRepository.deleteById(videoId);
                totalVideosProcessed++;
            } catch (Exception e) {
                logger.error("Error deleting video {}: {}", videoId, e.getMessage(), e);
            }
        }
        
        logger.info("Video synchronization completed. Processed {} videos.", totalVideosProcessed);
        return totalVideosProcessed;
    }
    
    /**
     * Sync playlists with three-way comparison
     */
    public int syncPlaylists() {
        logger.info("Starting playlist synchronization...");
        int totalPlaylistsProcessed = 0;

        // Step 1: Fetch ALL Playlist IDs from YouTube
        List<YouTubePlaylistResponse.PlaylistItem> youtubePlaylistItems = youTubeApiService.getAllPlaylists();
        Set<String> youtubePlaylistIds = youtubePlaylistItems.stream()
                .map(YouTubePlaylistResponse.PlaylistItem::getId)
                .collect(Collectors.toSet());
        logger.info("Fetched {} playlist IDs from YouTube.", youtubePlaylistIds.size());

        // Step 2: Fetch ALL Playlist IDs from the Database
        List<Playlist> dbPlaylists = playlistRepository.findAll();
        Set<String> dbPlaylistIds = dbPlaylists.stream()
                .map(Playlist::getPlaylistId)
                .collect(Collectors.toSet());
        logger.info("Fetched {} playlist IDs from the database.", dbPlaylistIds.size());

        // Step 3: Perform a Three-Way Comparison and Reconciliation

        // INSERT (New Playlists)
        Set<String> newPlaylistIds = new HashSet<>(youtubePlaylistIds);
        newPlaylistIds.removeAll(dbPlaylistIds);
        logger.info("Found {} new playlists to insert.", newPlaylistIds.size());
        for (String playlistId : newPlaylistIds) {
            try {
                Optional<YouTubePlaylistResponse.PlaylistItem> playlistItemOpt = youtubePlaylistItems.stream()
                        .filter(item -> playlistId.equals(item.getId()))
                        .findFirst();
                if (playlistItemOpt.isPresent()) {
                    Playlist playlist = new Playlist();
                    updatePlaylistFromApi(playlist, playlistItemOpt.get());
                    playlistRepository.save(playlist);
                    totalPlaylistsProcessed++;
                }
            } catch (Exception e) {
                logger.error("Error inserting new playlist {}: {}", playlistId, e.getMessage(), e);
            }
        }

        // UPDATE (Existing Playlists)
        Set<String> commonPlaylistIds = new HashSet<>(youtubePlaylistIds);
        commonPlaylistIds.retainAll(dbPlaylistIds);
        logger.info("Found {} common playlists to update.", commonPlaylistIds.size());
        for (String playlistId : commonPlaylistIds) {
            try {
                Optional<YouTubePlaylistResponse.PlaylistItem> playlistItemOpt = youtubePlaylistItems.stream()
                        .filter(item -> playlistId.equals(item.getId()))
                        .findFirst();
                if (playlistItemOpt.isPresent()) {
                    Optional<Playlist> existingPlaylist = playlistRepository.findById(playlistId);
                    existingPlaylist.ifPresent(playlist -> {
                        updatePlaylistFromApi(playlist, playlistItemOpt.get());
                        playlistRepository.save(playlist);
                    });
                    totalPlaylistsProcessed++;
                }
            } catch (Exception e) {
                logger.error("Error updating playlist {}: {}", playlistId, e.getMessage(), e);
            }
        }

        // DELETE (Removed Playlists)
        Set<String> removedPlaylistIds = new HashSet<>(dbPlaylistIds);
        removedPlaylistIds.removeAll(youtubePlaylistIds);
        logger.info("Found {} playlists to delete.", removedPlaylistIds.size());
        for (String playlistId : removedPlaylistIds) {
            try {
                playlistRepository.deleteById(playlistId);
                totalPlaylistsProcessed++;
            } catch (Exception e) {
                logger.error("Error deleting playlist {}: {}", playlistId, e.getMessage(), e);
            }
        }

        logger.info("Playlist synchronization completed. Processed {} playlists.", totalPlaylistsProcessed);
        return totalPlaylistsProcessed;
    }
    
    /**
     * Update ChannelStats entity from API response
     */
    private void updateChannelStatsFromApi(ChannelStats channelStats, YouTubeChannelResponse.ChannelItem channelItem) {
        channelStats.setChannelId(channelItem.getId());
        channelStats.setChannelTitle(channelItem.getSnippet().getTitle());
        channelStats.setDescription(channelItem.getSnippet().getDescription());
        channelStats.setCustomUrl(channelItem.getSnippet().getCustomUrl());
        channelStats.setCountry(channelItem.getSnippet().getCountry());
        
        if (channelItem.getSnippet().getThumbnails() != null) {
            channelStats.setThumbnailUrl(channelItem.getSnippet().getThumbnails().getBestThumbnailUrl());
        }
        
        if (channelItem.getSnippet().getPublishedAt() != null) {
            try {
                channelStats.setPublishedAt(LocalDateTime.parse(
                    channelItem.getSnippet().getPublishedAt().replace("Z", ""), ISO_FORMATTER));
            } catch (Exception e) {
                logger.warn("Failed to parse published date: {}", channelItem.getSnippet().getPublishedAt());
            }
        }
        
        if (channelItem.getStatistics() != null) {
            try {
                if (channelItem.getStatistics().getSubscriberCount() != null) {
                    channelStats.setSubscriberCount(Long.parseLong(channelItem.getStatistics().getSubscriberCount()));
                }
                if (channelItem.getStatistics().getVideoCount() != null) {
                    channelStats.setVideoCount(Long.parseLong(channelItem.getStatistics().getVideoCount()));
                }
                if (channelItem.getStatistics().getViewCount() != null) {
                    channelStats.setViewCount(Long.parseLong(channelItem.getStatistics().getViewCount()));
                }
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse statistics: {}", e.getMessage());
            }
        }
        
        channelStats.setLastUpdated(LocalDateTime.now());
    }
    
    /**
     * Update Video entity from API response
     */
    private void updateVideoFromApi(Video video, YouTubeVideoResponse.VideoItem videoItem) {
        String videoId = videoItem.getVideoId();
        if (videoId != null) {
            video.setVideoId(videoId);
        }
        
        if (videoItem.getSnippet() != null) {
            video.setTitle(videoItem.getSnippet().getTitle());
            video.setDescription(videoItem.getSnippet().getDescription());
            video.setPublishedAt(LocalDateTime.parse(videoItem.getSnippet().getPublishedAt().replace("Z", ""), ISO_FORMATTER));
            video.setChannelId(videoItem.getSnippet().getChannelId());
            if (videoItem.getSnippet().getThumbnails() != null) {
                video.setThumbnailUrl(videoItem.getSnippet().getThumbnails().getBestThumbnailUrl());
            }
        }
        
        if (videoItem.getStatistics() != null) {
            if (videoItem.getStatistics().getViewCount() != null) {
                video.setViewCount(Long.parseLong(videoItem.getStatistics().getViewCount()));
            }
            if (videoItem.getStatistics().getLikeCount() != null) {
                video.setLikeCount(Long.parseLong(videoItem.getStatistics().getLikeCount()));
            }
            if (videoItem.getStatistics().getCommentCount() != null) {
                video.setCommentCount(Long.parseLong(videoItem.getStatistics().getCommentCount()));
            }
        }
        
        video.setLastUpdated(LocalDateTime.now());
    }
    
    /**
     * Update Playlist entity from API response
     */
    private void updatePlaylistFromApi(Playlist playlist, YouTubePlaylistResponse.PlaylistItem playlistItem) {
        String playlistId = playlistItem.getId();
        if (playlistId != null) {
            playlist.setPlaylistId(playlistId);
        }
        
        if (playlistItem.getSnippet() != null) {
            playlist.setTitle(playlistItem.getSnippet().getTitle());
            playlist.setDescription(playlistItem.getSnippet().getDescription());
            playlist.setPublishedAt(LocalDateTime.parse(playlistItem.getSnippet().getPublishedAt().replace("Z", ""), ISO_FORMATTER));
            playlist.setChannelId(playlistItem.getSnippet().getChannelId());
            if (playlistItem.getSnippet().getThumbnails() != null) {
                playlist.setThumbnailUrl(playlistItem.getSnippet().getThumbnails().getBestThumbnailUrl());
            }
        }
        
        playlist.setLastUpdated(LocalDateTime.now());
    }

    public Optional<ChannelStats> getChannelStats() {
        return channelStatsRepository.findAll().stream().findFirst();
    }

    public boolean isApiConnected() {
        try {
            youTubeApiService.getChannelInfo();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Playlist> getPlaylists() {
        return playlistRepository.findAll();
    }

    public VideoRepository getVideoRepository() {
        return videoRepository;
    }

    public PlaylistRepository getPlaylistRepository() {
        return playlistRepository;
    }
}


