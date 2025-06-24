package com.randomvideos139.website.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "update_history")
public class UpdateHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "update_type", nullable = false)
    private String updateType; // CHANNEL_STATS, VIDEOS, PLAYLISTS, ALL
    
    @Column(name = "update_timestamp", nullable = false)
    private LocalDateTime updateTimestamp;
    
    @Column(name = "records_updated")
    private Integer recordsUpdated;
    
    @Column(name = "status", nullable = false)
    private String status; // SUCCESS, FAILED, PARTIAL
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "duration_seconds")
    private Long durationSeconds;
    
    // Constructors
    public UpdateHistory() {
        this.updateTimestamp = LocalDateTime.now();
        this.status = "IN_PROGRESS";
    }
    
    public UpdateHistory(String updateType) {
        this();
        this.updateType = updateType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUpdateType() {
        return updateType;
    }
    
    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }
    
    public LocalDateTime getUpdateTimestamp() {
        return updateTimestamp;
    }
    
    public void setUpdateTimestamp(LocalDateTime updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
    
    public Integer getRecordsUpdated() {
        return recordsUpdated;
    }
    
    public void setRecordsUpdated(Integer recordsUpdated) {
        this.recordsUpdated = recordsUpdated;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Long getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    // Helper methods
    public void markAsSuccess(int recordsUpdated, long durationSeconds) {
        this.status = "SUCCESS";
        this.recordsUpdated = recordsUpdated;
        this.durationSeconds = durationSeconds;
    }
    
    public void markAsFailed(String errorMessage, long durationSeconds) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
        this.durationSeconds = durationSeconds;
    }
    
    public void markAsPartial(int recordsUpdated, String errorMessage, long durationSeconds) {
        this.status = "PARTIAL";
        this.recordsUpdated = recordsUpdated;
        this.errorMessage = errorMessage;
        this.durationSeconds = durationSeconds;
    }
}

