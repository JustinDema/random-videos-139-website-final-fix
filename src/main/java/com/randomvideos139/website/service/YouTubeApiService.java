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
    
    /**
     * Get channel statistics and information
     */
    public YouTubeChannelResponse getChannelInfo() {
        try {
            String url = String.format("%s/channels?part=snippet,statistics&id=%s&key=%s",
                    YOUTUBE_API_BASE_URL, channelId, apiKey);
            
            logger.info("Fetching channel info from: {}", url.replace(apiKey, "***"));
            
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
    
    /**
     * Get all videos from the channel with pagination
     */
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
                
                logger.info("Fetching videos from: {}", url.replace(apiKey, "***"));
                
                YouTubeVideoResponse response = restTemplate.getForObject(url, YouTubeVideoResponse.class);
                
                if (response != null && response.getItems() != null) {
                    allVideos.addAll(response.getItems());
                    pageToken = response.getNextPageToken();
                    logger.info("Successfully fetched {} videos. Next page token: {}", response.getItems().size(), pageToken);
                } else {
                    pageToken = null; // No more pages or empty response
                }
                
            } catch (RestClientException e) {
                logger.error("Error fetching all videos: {}", e.getMessage());
                pageToken = null; // Stop pagination on error
            }
        } while (pageToken != null && !pageToken.isEmpty());
        
        // Enrich videos with statistics after fetching all of them
        return enrichVideosWithStatistics(allVideos);
    }
    
    /**
     * Get all playlists from the channel with pagination
     */
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
                
                logger.info("Fetching playlists from: {}", url.replace(apiKey, "***"));
                
                YouTubePlaylistResponse response = restTemplate.getForObject(url, YouTubePlaylistResponse.class);
                
                if (response != null && response.getItems() != null) {
                    allPlaylists.addAll(response.getItems());
                    pageToken = response.getNextPageToken();
                    logger.info("Successfully fetched {} playlists. Next page token: {}", response.getItems().size(), pageToken);
                } else {
                    pageToken = null; // No more pages or empty response
                }
                
            } catch (RestClientException e) {
                logger.error("Error fetching all playlists: {}", e.getMessage());
                pageToken = null; // Stop pagination on error
            }
        } while (pageToken != null && !pageToken.isEmpty());
        
        return allPlaylists;
    }
    
    /**
     * Get detailed video information including statistics
     */
    public YouTubeVideoResponse getVideoDetails(String videoIds) {
        try {
            String url = String.format("%s/videos?part=snippet,statistics,contentDetails&id=%s&key=%s",
                    YOUTUBE_API_BASE_URL, videoIds, apiKey);
            
            logger.debug("Fetching video details from: {}", url.replace(apiKey, "***"));
            
            return restTemplate.getForObject(url, YouTubeVideoResponse.class);
            
        } catch (RestClientException e) {
            logger.error("Error fetching video details: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Enrich video response with detailed statistics
     */
    private List<YouTubeVideoResponse.VideoItem> enrichVideosWithStatistics(List<YouTubeVideoResponse.VideoItem> videoItems) {
        if (videoItems == null || videoItems.isEmpty()) {
            return videoItems;
        }
        
        // Group video IDs into chunks of 50 for the API call
        final int CHUNK_SIZE = 50;
        List<List<YouTubeVideoResponse.VideoItem>> chunks = new ArrayList<>();
        for (int i = 0; i < videoItems.size(); i += CHUNK_SIZE) {
            chunks.add(videoItems.subList(i, Math.min(videoItems.size(), i + CHUNK_SIZE)));
        }

        List<YouTubeVideoResponse.VideoItem> enrichedVideos = new ArrayList<>();

        for (List<YouTubeVideoResponse.VideoItem> chunk : chunks) {
            StringBuilder videoIds = new StringBuilder();
            for (YouTubeVideoResponse.VideoItem item : chunk) {
                String videoId = item.getVideoId();
                if (videoId != null) {
                    if (videoIds.length() > 0) {
                        videoIds.append(",");
                    }
                    videoIds.append(videoId);
                }
            }

            if (videoIds.length() == 0) {
                continue;
            }

            YouTubeVideoResponse detailedResponse = getVideoDetails(videoIds.toString());

            if (detailedResponse != null && detailedResponse.getItems() != null) {
                for (YouTubeVideoResponse.VideoItem originalItem : chunk) {
                    String originalVideoId = originalItem.getVideoId();
                    for (YouTubeVideoResponse.VideoItem detailedItem : detailedResponse.getItems()) {
                        if (originalVideoId != null && originalVideoId.equals(detailedItem.getId())) {
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
                            break;
                        }
                    }
                    enrichedVideos.add(originalItem);
                }
            }
        }
        logger.info("Successfully enriched {} videos with detailed statistics", enrichedVideos.size());
        return enrichedVideos;
    }
    
    /**
     * Test API connectivity
     */
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


