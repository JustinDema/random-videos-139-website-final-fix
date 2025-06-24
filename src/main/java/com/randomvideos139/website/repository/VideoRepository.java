package com.randomvideos139.website.repository;

import com.randomvideos139.website.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {
    
    /**
     * Find videos by channel ID
     */
    List<Video> findByChannelIdOrderByPublishedAtDesc(String channelId);
    
    /**
     * Find latest videos by channel ID with limit
     */
    @Query("SELECT v FROM Video v WHERE v.channelId = :channelId ORDER BY v.publishedAt DESC LIMIT :limit")
    List<Video> findLatestByChannelId(@Param("channelId") String channelId, @Param("limit") int limit);
    
    /**
     * Find most popular videos by channel ID with limit (ordered by view count)
     */
    @Query("SELECT v FROM Video v WHERE v.channelId = :channelId ORDER BY v.viewCount DESC LIMIT :limit")
    List<Video> findMostPopularByChannelId(@Param("channelId") String channelId, @Param("limit") int limit);
    
    /**
     * Find videos marked as latest
     */
    List<Video> findByChannelIdAndIsLatestTrueOrderByPublishedAtDesc(String channelId);
    
    /**
     * Find videos marked as popular
     */
    List<Video> findByChannelIdAndIsPopularTrueOrderByViewCountDesc(String channelId);
    
    /**
     * Find videos published after a certain date
     */
    List<Video> findByChannelIdAndPublishedAtAfterOrderByPublishedAtDesc(String channelId, LocalDateTime publishedAfter);
    
    /**
     * Find videos that need statistics update (older than specified time)
     */
    @Query("SELECT v FROM Video v WHERE v.lastUpdated < :cutoffTime")
    List<Video> findVideosNeedingUpdate(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Count videos by channel ID
     */
    long countByChannelId(String channelId);
    
    /**
     * Find top videos by view count across all channels
     */
    @Query("SELECT v FROM Video v ORDER BY v.viewCount DESC LIMIT :limit")
    List<Video> findTopVideosByViews(@Param("limit") int limit);
    
    /**
     * Reset all latest flags for a channel
     */
    @Modifying
    @Query("UPDATE Video v SET v.isLatest = false WHERE v.channelId = :channelId")
    void resetLatestFlags(@Param("channelId") String channelId);
    
    /**
     * Reset all popular flags for a channel
     */
    @Modifying
    @Query("UPDATE Video v SET v.isPopular = false WHERE v.channelId = :channelId")
    void resetPopularFlags(@Param("channelId") String channelId);
    
    /**
     * Mark videos as latest
     */
    @Modifying
    @Query("UPDATE Video v SET v.isLatest = true WHERE v.videoId IN :videoIds")
    void markAsLatest(@Param("videoIds") List<String> videoIds);
    
    /**
     * Mark videos as popular
     */
    @Modifying
    @Query("UPDATE Video v SET v.isPopular = true WHERE v.videoId IN :videoIds")
    void markAsPopular(@Param("videoIds") List<String> videoIds);

    Page<Video> findByChannelIdOrderByViewCountDesc(String channelId, Pageable pageable);

    Page<Video> findByChannelIdAndTitleContainingIgnoreCase(String channelId, String title, Pageable pageable);

    Page<Video> findByChannelId(String channelId, Pageable pageable);

    List<Video> findTop6ByChannelIdAndVideoIdNotOrderByPublishedAtDesc(String channelId, String videoId);

    // New methods as per instructions
    List<Video> findTop12ByOrderByViewCountDesc();
    List<Video> findTop12ByOrderByPublishedAtDesc();
    @Query(value = "SELECT * FROM video ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<Video> findRandomVideos();
    
    // Search functionality
    Page<Video> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}

