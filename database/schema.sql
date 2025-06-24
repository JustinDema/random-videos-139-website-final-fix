-- MySQL Database Setup Script for Random Videos 139 Website
-- This script creates the database and all required tables

-- Create database
CREATE DATABASE IF NOT EXISTS randomvideos139 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE randomvideos139;

-- Create channel_stats table
CREATE TABLE IF NOT EXISTS channel_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_id VARCHAR(255) NOT NULL,
    channel_title VARCHAR(255) NOT NULL,
    subscriber_count BIGINT,
    video_count BIGINT,
    view_count BIGINT,
    description TEXT,
    published_at DATETIME,
    country VARCHAR(100),
    custom_url VARCHAR(255),
    thumbnail_url VARCHAR(500),
    last_updated DATETIME NOT NULL,
    
    INDEX idx_channel_id (channel_id),
    INDEX idx_last_updated (last_updated)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create videos table
CREATE TABLE IF NOT EXISTS videos (
    video_id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    published_at DATETIME,
    duration VARCHAR(50),
    view_count BIGINT,
    like_count BIGINT,
    comment_count BIGINT,
    thumbnail_url VARCHAR(500),
    channel_id VARCHAR(255),
    category_id VARCHAR(50),
    tags TEXT,
    is_latest BOOLEAN DEFAULT FALSE,
    is_popular BOOLEAN DEFAULT FALSE,
    last_updated DATETIME NOT NULL,
    
    INDEX idx_channel_id (channel_id),
    INDEX idx_published_at (published_at),
    INDEX idx_view_count (view_count),
    INDEX idx_is_latest (is_latest),
    INDEX idx_is_popular (is_popular),
    INDEX idx_last_updated (last_updated)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create playlists table
CREATE TABLE IF NOT EXISTS playlists (
    playlist_id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    published_at DATETIME,
    channel_id VARCHAR(255),
    thumbnail_url VARCHAR(500),
    item_count INT,
    privacy_status VARCHAR(50),
    last_updated DATETIME NOT NULL,
    
    INDEX idx_channel_id (channel_id),
    INDEX idx_published_at (published_at),
    INDEX idx_privacy_status (privacy_status),
    INDEX idx_last_updated (last_updated)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create playlist_video_ids table (for ElementCollection)
CREATE TABLE IF NOT EXISTS playlist_video_ids (
    playlist_id VARCHAR(255) NOT NULL,
    video_id VARCHAR(255) NOT NULL,
    
    PRIMARY KEY (playlist_id, video_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    INDEX idx_playlist_id (playlist_id),
    INDEX idx_video_id (video_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create update_history table
CREATE TABLE IF NOT EXISTS update_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    update_type VARCHAR(50) NOT NULL,
    update_timestamp DATETIME NOT NULL,
    records_updated INT,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    duration_seconds BIGINT,
    
    INDEX idx_update_type (update_type),
    INDEX idx_update_timestamp (update_timestamp),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add foreign key constraints
ALTER TABLE videos 
ADD CONSTRAINT fk_videos_channel_id 
FOREIGN KEY (channel_id) REFERENCES channel_stats(channel_id) 
ON DELETE SET NULL;

ALTER TABLE playlists 
ADD CONSTRAINT fk_playlists_channel_id 
FOREIGN KEY (channel_id) REFERENCES channel_stats(channel_id) 
ON DELETE SET NULL;

-- Create indexes for better performance
CREATE INDEX idx_videos_channel_published ON videos(channel_id, published_at DESC);
CREATE INDEX idx_videos_channel_views ON videos(channel_id, view_count DESC);
CREATE INDEX idx_playlists_channel_published ON playlists(channel_id, published_at DESC);

-- Insert initial channel data (optional)
INSERT IGNORE INTO channel_stats (
    channel_id, 
    channel_title, 
    subscriber_count, 
    video_count, 
    view_count, 
    description, 
    published_at, 
    country, 
    custom_url, 
    last_updated
) VALUES (
    'UClf_sqXl4kMB4nX9I49SnZQ',
    'Random Videos 139',
    1960,
    405,
    1259609,
    'Daily AI Character Songs - New uploads at 8:00 PM (Berlin Time)',
    '2022-04-21 00:00:00',
    'DE',
    '@randomvideos1392',
    NOW()
);

-- Create a view for latest videos
CREATE OR REPLACE VIEW latest_videos AS
SELECT v.* 
FROM videos v 
WHERE v.is_latest = TRUE 
ORDER BY v.published_at DESC;

-- Create a view for popular videos
CREATE OR REPLACE VIEW popular_videos AS
SELECT v.* 
FROM videos v 
WHERE v.is_popular = TRUE 
ORDER BY v.view_count DESC;

-- Create a view for public playlists
CREATE OR REPLACE VIEW public_playlists AS
SELECT p.* 
FROM playlists p 
WHERE p.privacy_status = 'public' 
ORDER BY p.published_at DESC;

-- Create a view for update statistics
CREATE OR REPLACE VIEW update_stats AS
SELECT 
    update_type,
    COUNT(*) as total_updates,
    SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as successful_updates,
    SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed_updates,
    AVG(duration_seconds) as avg_duration_seconds,
    MAX(update_timestamp) as last_update
FROM update_history 
GROUP BY update_type;

COMMIT;

