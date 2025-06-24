package com.randomvideos139.website.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "videos", indexes = {
    @Index(name = "idx_channel_id", columnList = "channel_id"),
    @Index(name = "idx_published_at", columnList = "published_at"),
    @Index(name = "idx_view_count", columnList = "view_count"),
    @Index(name = "idx_is_latest", columnList = "is_latest"),
    @Index(name = "idx_is_popular", columnList = "is_popular")
})
public class Video {
    
    @Id
    private String videoId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "duration")
    private String duration;
    
    @Column(name = "view_count")
    private Long viewCount;
    
    @Column(name = "like_count")
    private Long likeCount;
    
    @Column(name = "comment_count")
    private Long commentCount;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "channel_id")
    private String channelId;
    
    @Column(name = "category_id")
    private String categoryId;
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;
    
    @Column(name = "is_latest")
    private Boolean isLatest = false;
    
    @Column(name = "is_popular")
    private Boolean isPopular = false;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    // Constructors
    public Video() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public Video(String videoId, String title) {
        this();
        this.videoId = videoId;
        this.title = title;
    }
    
    // Getters and Setters
    public String getVideoId() {
        return videoId;
    }
    
    public void setVideoId(String videoId) {
        this.videoId = videoId;
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
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    public Long getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
    
    public Long getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
    
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
    
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public String getChannelId() {
        return channelId;
    }
    
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Boolean getIsLatest() {
        return isLatest;
    }
    
    public void setIsLatest(Boolean isLatest) {
        this.isLatest = isLatest;
    }
    
    public Boolean getIsPopular() {
        return isPopular;
    }
    
    public void setIsPopular(Boolean isPopular) {
        this.isPopular = isPopular;
    }
    
    // Helper methods for precise formatting
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
    
    public String getFormattedLikeCount() {
        if (likeCount == null) return "0";
        if (likeCount >= 1000000) {
            double millions = likeCount / 1000000.0;
            if (millions >= 10) {
                return String.format("%.0fM", millions);
            } else {
                return String.format("%.2fM", millions);
            }
        } else if (likeCount >= 1000) {
            double thousands = likeCount / 1000.0;
            if (thousands >= 10) {
                return String.format("%.1fK", thousands);
            } else {
                return String.format("%.2fK", thousands);
            }
        }
        return likeCount.toString();
    }
    
    public String getFormattedCommentCount() {
        if (commentCount == null) return "0";
        if (commentCount >= 1000000) {
            double millions = commentCount / 1000000.0;
            if (millions >= 10) {
                return String.format("%.0fM", millions);
            } else {
                return String.format("%.2fM", millions);
            }
        } else if (commentCount >= 1000) {
            double thousands = commentCount / 1000.0;
            if (thousands >= 10) {
                return String.format("%.1fK", thousands);
            } else {
                return String.format("%.2fK", thousands);
            }
        }
        return commentCount.toString();
    }
    
    public String getYouTubeUrl() {
        return "https://www.youtube.com/watch?v=" + videoId;
    }
    
    public String getShortDescription() {
        if (description == null || description.length() <= 150) {
            return description;
        }
        return description.substring(0, 150) + "...";
    }
    
    public String getTimeAgo() {
        if (publishedAt == null) return "Unknown";
        
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(publishedAt, now).toDays();
        
        if (days == 0) {
            long hours = java.time.Duration.between(publishedAt, now).toHours();
            if (hours == 0) {
                long minutes = java.time.Duration.between(publishedAt, now).toMinutes();
                return minutes + " minutes ago";
            }
            return hours + " hours ago";
        } else if (days == 1) {
            return "1 day ago";
        } else if (days < 7) {
            return days + " days ago";
        } else if (days < 30) {
            long weeks = days / 7;
            return weeks == 1 ? "1 week ago" : weeks + " weeks ago";
        } else if (days < 365) {
            long months = days / 30;
            return months == 1 ? "1 month ago" : months + " months ago";
        } else {
            long years = days / 365;
            return years == 1 ? "1 year ago" : years + " years ago";
        }
    }
    
    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }
}

