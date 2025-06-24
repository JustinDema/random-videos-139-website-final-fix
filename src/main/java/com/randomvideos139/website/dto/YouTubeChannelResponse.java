package com.randomvideos139.website.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeChannelResponse {
    
    @JsonProperty("items")
    private java.util.List<ChannelItem> items;
    
    public java.util.List<ChannelItem> getItems() {
        return items;
    }
    
    public void setItems(java.util.List<ChannelItem> items) {
        this.items = items;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChannelItem {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("snippet")
        private ChannelSnippet snippet;
        
        @JsonProperty("statistics")
        private ChannelStatistics statistics;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public ChannelSnippet getSnippet() {
            return snippet;
        }
        
        public void setSnippet(ChannelSnippet snippet) {
            this.snippet = snippet;
        }
        
        public ChannelStatistics getStatistics() {
            return statistics;
        }
        
        public void setStatistics(ChannelStatistics statistics) {
            this.statistics = statistics;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChannelSnippet {
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("description")
        private String description;
        
        @JsonProperty("customUrl")
        private String customUrl;
        
        @JsonProperty("publishedAt")
        private String publishedAt;
        
        @JsonProperty("country")
        private String country;
        
        @JsonProperty("thumbnails")
        private Thumbnails thumbnails;
        
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
        
        public String getCustomUrl() {
            return customUrl;
        }
        
        public void setCustomUrl(String customUrl) {
            this.customUrl = customUrl;
        }
        
        public String getPublishedAt() {
            return publishedAt;
        }
        
        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }
        
        public String getCountry() {
            return country;
        }
        
        public void setCountry(String country) {
            this.country = country;
        }
        
        public Thumbnails getThumbnails() {
            return thumbnails;
        }
        
        public void setThumbnails(Thumbnails thumbnails) {
            this.thumbnails = thumbnails;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChannelStatistics {
        @JsonProperty("viewCount")
        private String viewCount;
        
        @JsonProperty("subscriberCount")
        private String subscriberCount;
        
        @JsonProperty("videoCount")
        private String videoCount;
        
        public String getViewCount() {
            return viewCount;
        }
        
        public void setViewCount(String viewCount) {
            this.viewCount = viewCount;
        }
        
        public String getSubscriberCount() {
            return subscriberCount;
        }
        
        public void setSubscriberCount(String subscriberCount) {
            this.subscriberCount = subscriberCount;
        }
        
        public String getVideoCount() {
            return videoCount;
        }
        
        public void setVideoCount(String videoCount) {
            this.videoCount = videoCount;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnails {
        @JsonProperty("default")
        private Thumbnail defaultThumbnail;
        
        @JsonProperty("medium")
        private Thumbnail medium;
        
        @JsonProperty("high")
        private Thumbnail high;
        
        public Thumbnail getDefaultThumbnail() {
            return defaultThumbnail;
        }
        
        public void setDefaultThumbnail(Thumbnail defaultThumbnail) {
            this.defaultThumbnail = defaultThumbnail;
        }
        
        public Thumbnail getMedium() {
            return medium;
        }
        
        public void setMedium(Thumbnail medium) {
            this.medium = medium;
        }
        
        public Thumbnail getHigh() {
            return high;
        }
        
        public void setHigh(Thumbnail high) {
            this.high = high;
        }
        
        public String getBestThumbnailUrl() {
            if (high != null && high.getUrl() != null) return high.getUrl();
            if (medium != null && medium.getUrl() != null) return medium.getUrl();
            if (defaultThumbnail != null && defaultThumbnail.getUrl() != null) return defaultThumbnail.getUrl();
            return null;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnail {
        @JsonProperty("url")
        private String url;
        
        @JsonProperty("width")
        private Integer width;
        
        @JsonProperty("height")
        private Integer height;
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public Integer getWidth() {
            return width;
        }
        
        public void setWidth(Integer width) {
            this.width = width;
        }
        
        public Integer getHeight() {
            return height;
        }
        
        public void setHeight(Integer height) {
            this.height = height;
        }
    }
}

