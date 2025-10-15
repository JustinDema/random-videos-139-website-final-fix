
package com.randomvideos139.website.service;

import com.randomvideos139.website.entity.Video;
import com.randomvideos139.website.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public Page<Video> findVideos(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }

    public Page<Video> searchVideos(String query, Pageable pageable) {
        return videoRepository.findByTitleContainingIgnoreCase(query, pageable);
    }

    public Optional<Video> findVideoById(String videoId) {
        return videoRepository.findById(videoId);
    }
}
