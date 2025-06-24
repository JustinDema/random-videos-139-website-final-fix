package com.randomvideos139.website.repository;

import com.randomvideos139.website.entity.UpdateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UpdateHistoryRepository extends JpaRepository<UpdateHistory, Long> {
    
    List<UpdateHistory> findByUpdateTypeOrderByUpdateTimestampDesc(String updateType);
    
    @Query("SELECT u FROM UpdateHistory u WHERE u.updateType = ?1 AND u.status = 'SUCCESS' ORDER BY u.updateTimestamp DESC")
    Optional<UpdateHistory> findLastSuccessfulUpdate(String updateType);
    
    @Query("SELECT u FROM UpdateHistory u ORDER BY u.updateTimestamp DESC")
    List<UpdateHistory> findAllOrderByUpdateTimestampDesc();
    
    @Query("SELECT u FROM UpdateHistory u WHERE u.updateTimestamp >= ?1 ORDER BY u.updateTimestamp DESC")
    List<UpdateHistory> findRecentUpdates(LocalDateTime since);
    
    @Query("SELECT COUNT(u) FROM UpdateHistory u WHERE u.status = 'SUCCESS' AND u.updateTimestamp >= ?1")
    long countSuccessfulUpdatesSince(LocalDateTime since);
    
    @Query("SELECT COUNT(u) FROM UpdateHistory u WHERE u.status = 'FAILED' AND u.updateTimestamp >= ?1")
    long countFailedUpdatesSince(LocalDateTime since);
}

