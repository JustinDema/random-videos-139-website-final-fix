package com.randomvideos139.website.service;

import com.randomvideos139.website.dto.YouTubeChannelResponse;
import com.randomvideos139.website.dto.YouTubeVideoResponse;
import com.randomvideos139.website.dto.YouTubePlaylistResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class YouTubeApiService {

    private static final Logger logger = LoggerFactory.getLogger(YouTubeApiService.class);
    private static final String YOUTUBE_API_BASE_URL = "https://www.googleapis.com/youtube/v3";

    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.channel.id}")
    private String channelId;

    private final RestTemplate restTemplate;

    public YouTubeApiService() {
        this.restTemplate = new RestTemplate();
    }

    public YouTubeChannelResponse getChannelInfo() {
        try {
            String url = String.format("%s/channels?part=snippet,statistics&id=%s&key=%s",
                    YOUTUBE_API_BASE_URL, channelId, apiKey);
            logger.info("Fetching channel info...");
            YouTubeChannelResponse response = restTemplate.getForObject(url, YouTubeChannelResponse.class);
            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                logger.info("Successfully fetched channel info for: {}",
                        response.getItems().get(0).getSnippet().getTitle());
                return response;
            } else {
                logger.warn("No channel found for ID: {}", channelId);
                return null;
            }
        } catch (RestClientException e) {
            logger.error("Error fetching channel info: {}", e.getMessage());
            return null;
        }
    }

    public List<YouTubeVideoResponse.VideoItem> getAllVideos() {
        List<YouTubeVideoResponse.VideoItem> allVideos = new ArrayList<>();
        String pageToken = null;
        do {
            try {
                String url = String.format("%s/search?part=snippet&channelId=%s&order=date&type=video&maxResults=50&key=%s",
                        YOUTUBE_API_BASE_URL, channelId, apiKey);
                if (pageToken != null && !pageToken.isEmpty()) {
                    url += "&pageToken=" + pageToken;
                }
                logger.info("Fetching video list page...");
                YouTubeVideoResponse response = restTemplate.getForObject(url, YouTubeVideoResponse.class);
                if (response != null && response.getItems() != null) {
                    allVideos.addAll(response.getItems());
                    pageToken = response.getNextPageToken();
                    logger.info("Successfully fetched {} videos. Next page token: {}", response.getItems().size(), pageToken);
                } else {
                    pageToken = null;
                }
            } catch (RestClientException e) {
                logger.error("Error fetching video list: {}", e.getMessage());
                pageToken = null;
            }
        } while (pageToken != null && !pageToken.isEmpty());
        return enrichVideosWithStatistics(allVideos);
    }

    public List<YouTubePlaylistResponse.PlaylistItem> getAllPlaylists() {
        List<YouTubePlaylistResponse.PlaylistItem> allPlaylists = new ArrayList<>();
        String pageToken = null;
        do {
            try {
                String url = String.format("%s/playlists?part=snippet,contentDetails&channelId=%s&maxResults=50&key=%s",
                        YOUTUBE_API_BASE_URL, channelId, apiKey);
                if (pageToken != null && !pageToken.isEmpty()) {
                    url += "&pageToken=" + pageToken;
                }
                logger.info("Fetching playlists page...");
                YouTubePlaylistResponse response = restTemplate.getForObject(url, YouTubePlaylistResponse.class);
                if (response != null && response.getItems() != null) {
                    allPlaylists.addAll(response.getItems());
                    pageToken = response.getNextPageToken();
                    logger.info("Successfully fetched {} playlists. Next page token: {}", response.getItems().size(), pageToken);
                } else {
                    pageToken = null;
                }
            } catch (RestClientException e) {
                logger.error("Error fetching playlists: {}", e.getMessage());
                pageToken = null;
            }
        } while (pageToken != null && !pageToken.isEmpty());
        return allPlaylists;
    }

    public YouTubeVideoResponse getVideoDetails(String videoIds) {
        String url = String.format("%s/videos?part=snippet,statistics,contentDetails&id=%s&key=%s",
                YOUTUBE_API_BASE_URL, videoIds, apiKey);

        // This will print the exact link to your console for debugging
        System.out.println("\n\n******************************************************************");
        System.out.println("DEBUGGING YOUTUBE API URL (COPY AND PASTE IN BROWSER):");
        System.out.println(url);
        System.out.println("******************************************************************\n\n");

        try {
            logger.info("Fetching video details for a chunk of videos...");
            return restTemplate.getForObject(url, YouTubeVideoResponse.class);
        } catch (RestClientException e) {
            logger.error("!!! CRITICAL ERROR fetching video details. URL was: {} - Error: {}", url.replace(apiKey, "***"), e.getMessage());
            return null;
        }
    }

    private List<YouTubeVideoResponse.VideoItem> enrichVideosWithStatistics(List<YouTubeVideoResponse.VideoItem> videoItems) {
        if (videoItems == null || videoItems.isEmpty()) {
            logger.warn("enrichVideosWithStatistics called with no videos to process.");
            return videoItems;
        }

        logger.info("Starting enrichment process for {} videos...", videoItems.size());
        final int CHUNK_SIZE = 50;
        int enrichedCount = 0;

        for (int i = 0; i < videoItems.size(); i += CHUNK_SIZE) {
            List<YouTubeVideoResponse.VideoItem> chunk = videoItems.subList(i, Math.min(videoItems.size(), i + CHUNK_SIZE));

            String videoIdsStr = chunk.stream()
                    .map(YouTubeVideoResponse.VideoItem::getVideoId)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.joining(","));

            if (videoIdsStr.isEmpty()) {
                logger.warn("Skipping a chunk in enrichment because no valid video IDs were found.");
                continue;
            }

            YouTubeVideoResponse detailedResponse = getVideoDetails(videoIdsStr);

            if (detailedResponse != null && detailedResponse.getItems() != null && !detailedResponse.getItems().isEmpty()) {
                logger.info("Successfully fetched details for {} videos in the chunk.", detailedResponse.getItems().size());
                Map<String, YouTubeVideoResponse.VideoItem> detailedItemsMap = detailedResponse.getItems().stream()
                        .collect(Collectors.toMap(
                                item -> item.getId().toString(),
                                item -> item,
                                (item1, item2) -> item1 // handle duplicates if any
                        ));

                for (YouTubeVideoResponse.VideoItem originalItem : chunk) {
                    YouTubeVideoResponse.VideoItem detailedItem = detailedItemsMap.get(originalItem.getVideoId());
                    if (detailedItem != null) {
                        originalItem.setStatistics(detailedItem.getStatistics());
                        originalItem.setContentDetails(detailedItem.getContentDetails());
                        if (detailedItem.getSnippet() != null) {
                            if (originalItem.getSnippet().getTags() == null && detailedItem.getSnippet().getTags() != null) {
                                originalItem.getSnippet().setTags(detailedItem.getSnippet().getTags());
                            }
                            if (originalItem.getSnippet().getCategoryId() == null && detailedItem.getSnippet().getCategoryId() != null) {
                                originalItem.getSnippet().setCategoryId(detailedItem.getSnippet().getCategoryId());
                            }
                        }
                        enrichedCount++;
                    }
                }
            } else {
                logger.warn("Received no details for video IDs chunk starting with: {}", chunk.get(0).getVideoId());
            }
        }

        logger.info("Enrichment process complete. Successfully enriched {} out of {} videos.", enrichedCount, videoItems.size());
        return videoItems;
    }

    public boolean testApiConnection() {
        try {
            YouTubeChannelResponse response = getChannelInfo();
            return response != null && response.getItems() != null && !response.getItems().isEmpty();
        } catch (Exception e) {
            logger.error("API connection test failed: {}", e.getMessage());
            return false;
        }
    }
}