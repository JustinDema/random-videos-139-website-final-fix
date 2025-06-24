package com.randomvideos139.website.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlists")
public class Playlist {
    
    @Id
    private String playlistId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "channel_id")
    private String channelId;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "item_count")
    private Integer itemCount;
    
    @Column(name = "privacy_status")
    private String privacyStatus;
    
    @ElementCollection
    @CollectionTable(name = "playlist_video_ids", joinColumns = @JoinColumn(name = "playlist_id"))
    @Column(name = "video_id")
    private List<String> videoIds = new ArrayList<>();
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    // Constructors
    public Playlist() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public Playlist(String playlistId, String title) {
        this();
        this.playlistId = playlistId;
        this.title = title;
    }
    
    // Getters and Setters
    public String getPlaylistId() {
        return playlistId;
    }
    
    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }
    
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
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    public String getChannelId() {
        return channelId;
    }
    
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public Integer getItemCount() {
        return itemCount;
    }
    
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
    
    public String getPrivacyStatus() {
        return privacyStatus;
    }
    
    public void setPrivacyStatus(String privacyStatus) {
        this.privacyStatus = privacyStatus;
    }
    
    public List<String> getVideoIds() {
        return videoIds;
    }
    
    public void setVideoIds(List<String> videoIds) {
        this.videoIds = videoIds;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    // Helper methods
    public String getYouTubeUrl() {
        return "https://www.youtube.com/playlist?list=" + playlistId;
    }
    
    public String getShortDescription() {
        if (description == null || description.length() <= 100) {
            return description;
        }
        return description.substring(0, 100) + "...";
    }
    
    public String getFormattedItemCount() {
        if (itemCount == null) return "0 videos";
        return itemCount == 1 ? "1 video" : itemCount + " videos";
    }
    
    public boolean isPublic() {
        return "public".equalsIgnoreCase(privacyStatus);
    }
    
    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }
}

