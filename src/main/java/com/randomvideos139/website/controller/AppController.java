
package com.randomvideos139.website.controller;

import com.randomvideos139.website.config.SocialConfig;
import com.randomvideos139.website.entity.ChannelStats;
import com.randomvideos139.website.entity.Playlist;
import com.randomvideos139.website.entity.Video;
import com.randomvideos139.website.service.DataSyncService;
import com.randomvideos139.website.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class AppController {

    @Autowired
    private DataSyncService dataSyncService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private SocialConfig socialConfig;

    @GetMapping("/")
    public String home(Model model) {
        // Latest Videos
        Pageable latestPageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Video> latestVideos = videoService.findVideos(latestPageable);
        model.addAttribute("latestVideos", latestVideos.getContent());

        // Popular Videos
        Pageable popularPageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "viewCount"));
        Page<Video> popularVideos = videoService.findVideos(popularPageable);
        model.addAttribute("popularVideos", popularVideos.getContent());

        // Channel Stats
        Optional<ChannelStats> channelStats = dataSyncService.getChannelStats();
        channelStats.ifPresent(stats -> model.addAttribute("channelStats", stats));

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("socialLinks", socialConfig.getLinks());
        return "about";
    }

    @GetMapping("/playlists")
    public String playlists(Model model) {
        List<Playlist> playlists = dataSyncService.getPlaylists();
        model.addAttribute("playlists", playlists);
        return "playlists";
    }
    
    @GetMapping("/latest-videos")
    public String latestVideos(Model model) {
        return "redirect:/videos?sortBy=publishedAt&sortDir=desc";
    }

    @GetMapping("/popular-videos")
    public String popularVideos(Model model) {
        return "redirect:/videos?sortBy=viewCount&sortDir=desc";
    }
}
