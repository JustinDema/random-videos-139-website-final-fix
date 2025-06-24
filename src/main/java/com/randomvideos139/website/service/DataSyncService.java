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

    public void syncAllData() {
        logger.info("Starting full data synchronization...");
        UpdateHistory updateHistory = new UpdateHistory("ALL");
        updateHistoryRepository.save(updateHistory);
        long startTime = System.currentTimeMillis();
        int totalRecordsUpdated = 0;
        try {
            syncChannelStats();
            totalRecordsUpdated += 1;
            totalRecordsUpdated += syncVideos();
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
            logger.error("A critical error occurred during data synchronization: {}", e.getMessage(), e);
        }
    }

    public void syncChannelStats() {
        logger.info("Syncing channel statistics...");
        try {
            YouTubeChannelResponse response = youTubeApiService.getChannelInfo();
            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                YouTubeChannelResponse.ChannelItem channelItem = response.getItems().get(0);
                Optional<ChannelStats> existingStats = channelStatsRepository.findByChannelId(channelItem.getId());
                ChannelStats channelStats = existingStats.orElse(new ChannelStats());
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

    public int syncVideos() {
        logger.info("Starting video synchronization...");
        int totalVideosProcessed = 0;
        List<YouTubeVideoResponse.VideoItem> youtubeVideoItems = youTubeApiService.getAllVideos();
        if (youtubeVideoItems == null || youtubeVideoItems.isEmpty()) {
            logger.warn("No videos returned from YouTube API. Aborting video sync.");
            return 0;
        }
        Set<String> youtubeVideoIds = youtubeVideoItems.stream()
                .map(YouTubeVideoResponse.VideoItem::getVideoId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        logger.info("Fetched {} video IDs from YouTube.", youtubeVideoIds.size());
        List<Video> dbVideos = videoRepository.findAll();
        Set<String> dbVideoIds = dbVideos.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toSet());
        logger.info("Found {} video IDs in the database.", dbVideoIds.size());

        Set<String> newVideoIds = new HashSet<>(youtubeVideoIds);
        newVideoIds.removeAll(dbVideoIds);
        logger.info("Found {} new videos to insert.", newVideoIds.size());
        for (String videoId : newVideoIds) {
            try {
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
                logger.error("Failed to process and save NEW video {}. Skipping record.", videoId, e);
            }
        }

        Set<String> commonVideoIds = new HashSet<>(youtubeVideoIds);
        commonVideoIds.retainAll(dbVideoIds);
        logger.info("Found {} common videos to update.", commonVideoIds.size());
        for (String videoId : commonVideoIds) {
            try {
                Optional<YouTubeVideoResponse.VideoItem> videoItemOpt = youtubeVideoItems.stream()
                        .filter(item -> videoId.equals(item.getVideoId()))
                        .findFirst();
                if (videoItemOpt.isPresent()) {
                    videoRepository.findById(videoId).ifPresent(video -> {
                        updateVideoFromApi(video, videoItemOpt.get());
                        videoRepository.save(video);
                    });
                    totalVideosProcessed++;
                }
            } catch (Exception e) {
                logger.error("Failed to process and save EXISTING video {}. Skipping record.", videoId, e);
            }
        }

        Set<String> removedVideoIds = new HashSet<>(dbVideoIds);
        removedVideoIds.removeAll(youtubeVideoIds);
        logger.info("Found {} videos to delete.", removedVideoIds.size());
        for (String videoId : removedVideoIds) {
            try {
                videoRepository.deleteById(videoId);
                totalVideosProcessed++;
            } catch (Exception e) {
                logger.error("Failed to delete video {}.", videoId, e);
            }
        }

        logger.info("Video synchronization completed. Processed {} videos.", totalVideosProcessed);
        return totalVideosProcessed;
    }

    public int syncPlaylists() {
        logger.info("Starting playlist synchronization...");
        int totalPlaylistsProcessed = 0;
        List<YouTubePlaylistResponse.PlaylistItem> youtubePlaylistItems = youTubeApiService.getAllPlaylists();
        if (youtubePlaylistItems == null || youtubePlaylistItems.isEmpty()) {
            logger.warn("No playlists returned from YouTube API. Aborting playlist sync.");
            return 0;
        }
        Set<String> youtubePlaylistIds = youtubePlaylistItems.stream()
                .map(YouTubePlaylistResponse.PlaylistItem::getId)
                .collect(Collectors.toSet());
        List<Playlist> dbPlaylists = playlistRepository.findAll();
        Set<String> dbPlaylistIds = dbPlaylists.stream()
                .map(Playlist::getPlaylistId)
                .collect(Collectors.toSet());

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
                logger.error("Failed to process and save NEW playlist {}. Skipping record.", playlistId, e);
            }
        }

        Set<String> commonPlaylistIds = new HashSet<>(youtubePlaylistIds);
        commonPlaylistIds.retainAll(dbPlaylistIds);
        logger.info("Found {} common playlists to update.", commonPlaylistIds.size());
        for (String playlistId : commonPlaylistIds) {
            try {
                Optional<YouTubePlaylistResponse.PlaylistItem> playlistItemOpt = youtubePlaylistItems.stream()
                        .filter(item -> playlistId.equals(item.getId()))
                        .findFirst();
                if (playlistItemOpt.isPresent()) {
                    playlistRepository.findById(playlistId).ifPresent(playlist -> {
                        updatePlaylistFromApi(playlist, playlistItemOpt.get());
                        playlistRepository.save(playlist);
                    });
                    totalPlaylistsProcessed++;
                }
            } catch (Exception e) {
                logger.error("Failed to process and save EXISTING playlist {}. Skipping record.", playlistId, e);
            }
        }

        Set<String> removedPlaylistIds = new HashSet<>(dbPlaylistIds);
        removedPlaylistIds.removeAll(youtubePlaylistIds);
        logger.info("Found {} playlists to delete.", removedPlaylistIds.size());
        for (String playlistId : removedPlaylistIds) {
            try {
                playlistRepository.deleteById(playlistId);
                totalPlaylistsProcessed++;
            } catch (Exception e) {
                logger.error("Failed to delete playlist {}.", playlistId, e);
            }
        }
        logger.info("Playlist synchronization completed. Processed {} playlists.", totalPlaylistsProcessed);
        return totalPlaylistsProcessed;
    }

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
                logger.warn("Could not parse 'publishedAt' date for channel: {}", channelItem.getSnippet().getPublishedAt());
            }
        }
        if (channelItem.getStatistics() != null) {
            try {
                String subscriberCountStr = channelItem.getStatistics().getSubscriberCount();
                if (subscriberCountStr != null) channelStats.setSubscriberCount(Long.parseLong(subscriberCountStr));

                String videoCountStr = channelItem.getStatistics().getVideoCount();
                if (videoCountStr != null) channelStats.setVideoCount(Long.parseLong(videoCountStr));

                String viewCountStr = channelItem.getStatistics().getViewCount();
                if (viewCountStr != null) channelStats.setViewCount(Long.parseLong(viewCountStr));
            } catch (NumberFormatException e) {
                logger.warn("Could not parse channel statistics: {}", e.getMessage());
            }
        }
        channelStats.setLastUpdated(LocalDateTime.now());
    }

    private void updateVideoFromApi(Video video, YouTubeVideoResponse.VideoItem videoItem) {
        video.setVideoId(videoItem.getVideoId());

        if (videoItem.getSnippet() != null) {
            video.setTitle(videoItem.getSnippet().getTitle());
            video.setDescription(videoItem.getSnippet().getDescription());
            if (videoItem.getSnippet().getPublishedAt() != null) {
                try {
                    video.setPublishedAt(LocalDateTime.parse(videoItem.getSnippet().getPublishedAt().replace("Z", ""), ISO_FORMATTER));
                } catch (Exception e) {
                    logger.warn("Could not parse 'publishedAt' date for video {}: {}", video.getVideoId(), videoItem.getSnippet().getPublishedAt());
                }
            }
            video.setChannelId(videoItem.getSnippet().getChannelId());
            if (videoItem.getSnippet().getThumbnails() != null) {
                video.setThumbnailUrl(videoItem.getSnippet().getThumbnails().getBestThumbnailUrl());
            }
            if (videoItem.getSnippet().getTags() != null && !videoItem.getSnippet().getTags().isEmpty()) {
                video.setTags(String.join(",", videoItem.getSnippet().getTags()));
            }
            video.setCategoryId(videoItem.getSnippet().getCategoryId());
        }

        if (videoItem.getStatistics() != null) {
            try {
                String viewCountStr = videoItem.getStatistics().getViewCount();
                if (viewCountStr != null) video.setViewCount(Long.parseLong(viewCountStr));

                String likeCountStr = videoItem.getStatistics().getLikeCount();
                if (likeCountStr != null) video.setLikeCount(Long.parseLong(likeCountStr));

                String commentCountStr = videoItem.getStatistics().getCommentCount();
                if (commentCountStr != null) video.setCommentCount(Long.parseLong(commentCountStr));
            } catch (NumberFormatException e) {
                logger.warn("Could not parse video statistics for video ID {}: {}", video.getVideoId(), e.getMessage());
            }
        }

        if (videoItem.getContentDetails() != null && videoItem.getContentDetails().getDuration() != null) {
            video.setDuration(videoItem.getContentDetails().getFormattedDuration());
        }

        video.setLastUpdated(LocalDateTime.now());
    }

    private void updatePlaylistFromApi(Playlist playlist, YouTubePlaylistResponse.PlaylistItem playlistItem) {
        playlist.setPlaylistId(playlistItem.getId());
        if (playlistItem.getSnippet() != null) {
            playlist.setTitle(playlistItem.getSnippet().getTitle());
            playlist.setDescription(playlistItem.getSnippet().getDescription());
            if (playlistItem.getSnippet().getPublishedAt() != null) {
                try {
                    playlist.setPublishedAt(LocalDateTime.parse(playlistItem.getSnippet().getPublishedAt().replace("Z", ""), ISO_FORMATTER));
                } catch (Exception e) {
                    logger.warn("Could not parse 'publishedAt' date for playlist {}: {}", playlist.getPlaylistId(), playlistItem.getSnippet().getPublishedAt());
                }
            }
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