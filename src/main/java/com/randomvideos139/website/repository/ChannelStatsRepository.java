package com.randomvideos139.website.repository;

import com.randomvideos139.website.entity.ChannelStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelStatsRepository extends JpaRepository<ChannelStats, Long> {
    
    /**
     * Find channel stats by channel ID
     */
    Optional<ChannelStats> findByChannelId(String channelId);
    
    /**
     * Find the latest channel stats entry
     */
    @Query("SELECT c FROM ChannelStats c ORDER BY c.lastUpdated DESC LIMIT 1")
    Optional<ChannelStats> findLatest();
    
    /**
     * Check if channel stats exist for a given channel ID
     */
    boolean existsByChannelId(String channelId);
}

