package com.randomvideos139.website.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "channel_stats")
public class ChannelStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "channel_id", nullable = false)
    private String channelId;
    
    @Column(name = "channel_title", nullable = false)
    private String channelTitle;
    
    @Column(name = "subscriber_count")
    private Long subscriberCount;
    
    @Column(name = "video_count")
    private Long videoCount;
    
    @Column(name = "view_count")
    private Long viewCount;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "custom_url")
    private String customUrl;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    // Constructors
    public ChannelStats() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public ChannelStats(String channelId, String channelTitle) {
        this();
        this.channelId = channelId;
        this.channelTitle = channelTitle;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Long getSubscriberCount() {
        return subscriberCount;
    }
    
    public void setSubscriberCount(Long subscriberCount) {
        this.subscriberCount = subscriberCount;
    }
    
    public Long getVideoCount() {
        return videoCount;
    }
    
    public void setVideoCount(Long videoCount) {
        this.videoCount = videoCount;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
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
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getCustomUrl() {
        return customUrl;
    }
    
    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    // Helper methods for precise formatting
    public String getFormattedSubscriberCount() {
        if (subscriberCount == null) return "0";
        if (subscriberCount >= 1000000) {
            double millions = subscriberCount / 1000000.0;
            if (millions >= 10) {
                return String.format("%.0fM", millions);
            } else {
                return String.format("%.2fM", millions);
            }
        } else if (subscriberCount >= 1000) {
            double thousands = subscriberCount / 1000.0;
            if (thousands >= 10) {
                return String.format("%.1fK", thousands);
            } else {
                return String.format("%.2fK", thousands);
            }
        }
        return subscriberCount.toString();
    }
    
    public String getFormattedViewCount() {
        if (viewCount == null) return "0";
        if (viewCount >= 1000000) {
            double millions = viewCount / 1000000.0;
            if (millions >= 10) {
                return String.format("%.0fM", millions);
            } else {
                return String.format("%.2fM", millions);
            }
        } else if (viewCount >= 1000) {
            double thousands = viewCount / 1000.0;
            if (thousands >= 10) {
                return String.format("%.1fK", thousands);
            } else {
                return String.format("%.2fK", thousands);
            }
        }
        return viewCount.toString();
    }
    
    public String getFormattedVideoCount() {
        if (videoCount == null) return "0";
        if (videoCount >= 1000000) {
            double millions = videoCount / 1000000.0;
            if (millions >= 10) {
                return String.format("%.0fM", millions);
            } else {
                return String.format("%.2fM", millions);
            }
        } else if (videoCount >= 1000) {
            double thousands = videoCount / 1000.0;
            if (thousands >= 10) {
                return String.format("%.1fK", thousands);
            } else {
                return String.format("%.2fK", thousands);
            }
        }
        return videoCount.toString();
    }
    
    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }
}

