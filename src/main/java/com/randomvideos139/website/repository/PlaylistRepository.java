package com.randomvideos139.website.repository;

import com.randomvideos139.website.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    
    /**
     * Find playlists by channel ID
     */
    List<Playlist> findByChannelIdOrderByPublishedAtDesc(String channelId);
    
    /**
     * Find public playlists by channel ID
     */
    @Query("SELECT p FROM Playlist p WHERE p.channelId = :channelId AND p.privacyStatus = 'public' ORDER BY p.publishedAt DESC")
    List<Playlist> findPublicPlaylistsByChannelId(@Param("channelId") String channelId);
    
    /**
     * Find playlists with most items
     */
    @Query("SELECT p FROM Playlist p WHERE p.channelId = :channelId ORDER BY p.itemCount DESC LIMIT :limit")
    List<Playlist> findPlaylistsByItemCount(@Param("channelId") String channelId, @Param("limit") int limit);
    
    /**
     * Find playlists that need update (older than specified time)
     */
    @Query("SELECT p FROM Playlist p WHERE p.lastUpdated < :cutoffTime")
    List<Playlist> findPlaylistsNeedingUpdate(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Count playlists by channel ID
     */
    long countByChannelId(String channelId);
    
    /**
     * Count public playlists by channel ID
     */
    @Query("SELECT COUNT(p) FROM Playlist p WHERE p.channelId = :channelId AND p.privacyStatus = 'public'")
    long countPublicPlaylistsByChannelId(@Param("channelId") String channelId);
}

