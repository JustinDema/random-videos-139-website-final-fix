package com.randomvideos139.website.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubePlaylistResponse {
    
    @JsonProperty("items")
    private java.util.List<PlaylistItem> items;
    
    @JsonProperty("nextPageToken")
    private String nextPageToken;
    
    public java.util.List<PlaylistItem> getItems() {
        return items;
    }
    
    public void setItems(java.util.List<PlaylistItem> items) {
        this.items = items;
    }
    
    public String getNextPageToken() {
        return nextPageToken;
    }
    
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaylistItem {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("snippet")
        private PlaylistSnippet snippet;
        
        @JsonProperty("contentDetails")
        private PlaylistContentDetails contentDetails;
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public PlaylistSnippet getSnippet() {
            return snippet;
        }
        
        public void setSnippet(PlaylistSnippet snippet) {
            this.snippet = snippet;
        }
        
        public PlaylistContentDetails getContentDetails() {
            return contentDetails;
        }
        
        public void setContentDetails(PlaylistContentDetails contentDetails) {
            this.contentDetails = contentDetails;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaylistSnippet {
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
        
        public YouTubeChannelResponse.Thumbnails getThumbnails() {
            return thumbnails;
        }
        
        public void setThumbnails(YouTubeChannelResponse.Thumbnails thumbnails) {
            this.thumbnails = thumbnails;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaylistContentDetails {
        @JsonProperty("itemCount")
        private Integer itemCount;
        
        public Integer getItemCount() {
            return itemCount;
        }
        
        public void setItemCount(Integer itemCount) {
            this.itemCount = itemCount;
        }
    }
}

