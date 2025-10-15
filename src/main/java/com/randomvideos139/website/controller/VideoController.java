
package com.randomvideos139.website.controller;

import com.randomvideos139.website.service.VideoService;
import com.randomvideos139.website.entity.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping
    public String listVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, 12, Sort.by(direction, sortBy));

        Page<Video> videoPage;
        if (search != null && !search.trim().isEmpty()) {
            videoPage = videoService.searchVideos(search.trim(), pageable);
            model.addAttribute("search", search);
        } else {
            videoPage = videoService.findVideos(pageable);
        }

        model.addAttribute("videos", videoPage);
        return "all-videos";
    }

    @GetMapping("/{videoId}")
    public String videoDetail(@PathVariable String videoId, Model model) {
        Optional<Video> videoOptional = videoService.findVideoById(videoId);
        if (videoOptional.isPresent()) {
            model.addAttribute("video", videoOptional.get());
            return "video-detail";
        } else {
            return "redirect:/videos";
        }
    }
}
