package com.randomvideos139.website.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeVideoResponse {
    
    @JsonProperty("items")
    private java.util.List<VideoItem> items;
    
    @JsonProperty("nextPageToken")
    private String nextPageToken;
    
    public java.util.List<VideoItem> getItems() {
        return items;
    }
    
    public void setItems(java.util.List<VideoItem> items) {
        this.items = items;
    }
    
    public String getNextPageToken() {
        return nextPageToken;
    }
    
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoItem {
        @JsonProperty("id")
        private Object id; // Can be VideoId object or String
        
        @JsonProperty("snippet")
        private VideoSnippet snippet;
        
        @JsonProperty("statistics")
        private VideoStatistics statistics;
        
        @JsonProperty("contentDetails")
        private VideoContentDetails contentDetails;
        
        public Object getId() {
            return id;
        }
        
        public void setId(Object id) {
            this.id = id;
        }
        
        public VideoSnippet getSnippet() {
            return snippet;
        }
        
        public void setSnippet(VideoSnippet snippet) {
            this.snippet = snippet;
        }
        
        public VideoStatistics getStatistics() {
            return statistics;
        }
        
        public void setStatistics(VideoStatistics statistics) {
            this.statistics = statistics;
        }
        
        public VideoContentDetails getContentDetails() {
            return contentDetails;
        }
        
        public void setContentDetails(VideoContentDetails contentDetails) {
            this.contentDetails = contentDetails;
        }
        
        public String getVideoId() {
            if (id instanceof VideoId) {
                return ((VideoId) id).getVideoId();
            } else if (id instanceof String) {
                return (String) id;
            }
            return null;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoId {
        @JsonProperty("videoId")
        private String videoId;
        
        public String getVideoId() {
            return videoId;
        }
        
        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoSnippet {
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("publishedAt")
        private String publishedAt;
        
        @JsonProperty("channelId")
        private String channelId;
        
        @JsonProperty("channelTitle")
        private String channelTitle;
        
        @JsonProperty("categoryId")
        private String categoryId;
        
        @JsonProperty("tags")
        private java.util.List<String> tags;
        
        @JsonProperty("thumbnails")
        private YouTubeChannelResponse.Thumbnails thumbnails;
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getPublishedAt() {
            return publishedAt;
        }
        
        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }
        
        public String getChannelId() {
            return channelId;
        }
        
        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }
        
        public String getChannelTitle() {
            return channelTitle;
        }
        
        public void setChannelTitle(String channelTitle) {
            this.channelTitle = channelTitle;
        }
        
        public String getCategoryId() {
            return categoryId;
        }
        
        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }
        
        public java.util.List<String> getTags() {
            return tags;
        }
        
        public void setTags(java.util.List<String> tags) {
            this.tags = tags;
        }
        
        public YouTubeChannelResponse.Thumbnails getThumbnails() {
            return thumbnails;
        }
        
        public void setThumbnails(YouTubeChannelResponse.Thumbnails thumbnails) {
            this.thumbnails = thumbnails;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoStatistics {
        @JsonProperty("viewCount")
        private String viewCount;
        
        @JsonProperty("likeCount")
        private String likeCount;
        
        @JsonProperty("commentCount")
        private String commentCount;
        
        public String getViewCount() {
            return viewCount;
        }
        
        public void setViewCount(String viewCount) {
            this.viewCount = viewCount;
        }
        
        public String getLikeCount() {
            return likeCount;
        }
        
        public void setLikeCount(String likeCount) {
            this.likeCount = likeCount;
        }
        
        public String getCommentCount() {
            return commentCount;
        }
        
        public void setCommentCount(String commentCount) {
            this.commentCount = commentCount;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoContentDetails {
        @JsonProperty("duration")
        private String duration;
        
        public String getDuration() {
            return duration;
        }
        
        public void setDuration(String duration) {
            this.duration = duration;
        }
        
        public String getFormattedDuration() {
            if (duration == null) return "0:00";
            
            // Parse ISO 8601 duration format (PT4M13S)
            String formatted = duration.replace("PT", "");
            
            int hours = 0, minutes = 0, seconds = 0;
            
            if (formatted.contains("H")) {
                String[] parts = formatted.split("H");
                hours = Integer.parseInt(parts[0]);
                formatted = parts[1];
            }
            
            if (formatted.contains("M")) {
                String[] parts = formatted.split("M");
                minutes = Integer.parseInt(parts[0]);
                formatted = parts[1];
            }
            
            if (formatted.contains("S")) {
                seconds = Integer.parseInt(formatted.replace("S", ""));
            }
            
            if (hours > 0) {
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format("%d:%02d", minutes, seconds);
            }
        }
    }
}

