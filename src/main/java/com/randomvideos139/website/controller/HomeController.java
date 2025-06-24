package com.randomvideos139.website.controller;

import com.randomvideos139.website.entity.ChannelStats;
import com.randomvideos139.website.entity.Video;
import com.randomvideos139.website.entity.Playlist;
import com.randomvideos139.website.service.DataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private DataSyncService dataSyncService;

    @Value("${youtube.channel.name:Random Videos 139}")
    private String channelName;

    @Value("${youtube.channel.url:https://www.youtube.com/@randomvideos1392}")
    private String channelUrl;

    @Value("${social.facebook.url:https://facebook.com/Random.Videos.139}")
    private String facebookUrl;

    @Value("${social.instagram.url:https://instagram.com/random_videos_139}")
    private String instagramUrl;

    @Value("${social.twitter.url:https://twitter.com/139Videos}")
    private String twitterUrl;

    @Value("${social.reddit.url:https://reddit.com/user/AdriralSilverWolf}")
    private String redditUrl;

    @Value("${social.tiktok.url:https://tiktok.com/@random.videos.139}")
    private String tiktokUrl;

    @Value("${social.tumblr.url:https://tumblr.com/randomvideos139}")
    private String tumblrUrl;

    /**
     * Home page with featured videos carousel
     */
    @GetMapping("/")
    public String home(Model model) {
        addCommonAttributes(model);
        
        model.addAttribute("pageTitle", "Home");
        model.addAttribute("pageDescription", "Welcome to Random Videos 139 - Your destination for unique AI character songs featuring your favorite anime and manga characters!");
        
        // Get featured videos (latest 6 videos for carousel)
        Pageable featuredPageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Video> featuredVideosPage = dataSyncService.getVideoRepository().findAll(featuredPageable);
        model.addAttribute("featuredVideos", featuredVideosPage.getContent());
        
        // Get latest videos (for latest section)
        Pageable latestPageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Video> latestVideosPage = dataSyncService.getVideoRepository().findAll(latestPageable);
        model.addAttribute("latestVideos", latestVideosPage.getContent());
        
        // Get popular videos (for popular section)
        Pageable popularPageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "viewCount"));
        Page<Video> popularVideosPage = dataSyncService.getVideoRepository().findAll(popularPageable);
        model.addAttribute("popularVideos", popularVideosPage.getContent());
        
        // Get channel stats
        Optional<ChannelStats> channelStats = dataSyncService.getChannelStats();
        channelStats.ifPresent(stats -> {
            model.addAttribute("channelStats", stats);
            model.addAttribute("subscriberCount", stats.getFormattedSubscriberCount());
            model.addAttribute("videoCount", stats.getFormattedVideoCount());
            model.addAttribute("viewCount", stats.getFormattedViewCount());
        });
        
        return "index";
    }

    /**
     * Latest videos page
     */
    @GetMapping("/latest-videos")
    public String latestVideos(Model model, @RequestParam(defaultValue = "0") int page) {
        addCommonAttributes(model);
        
        model.addAttribute("pageTitle", "Latest Videos");
        model.addAttribute("pageDescription", "Discover our newest AI character songs featuring the latest anime and manga characters!");
        
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<Video> videosPage = dataSyncService.getVideoRepository().findAll(pageable);
        
        model.addAttribute("videos", videosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", videosPage.getTotalPages());
        model.addAttribute("totalElements", videosPage.getTotalElements());
        model.addAttribute("hasNext", videosPage.hasNext());
        model.addAttribute("hasPrevious", videosPage.hasPrevious());
        
        return "latest-videos";
    }

    /**
     * Popular videos page
     */
    @GetMapping("/popular-videos")
    public String popularVideos(Model model, @RequestParam(defaultValue = "0") int page) {
        addCommonAttributes(model);
        
        model.addAttribute("pageTitle", "Popular Videos");
        model.addAttribute("pageDescription", "Our most-watched AI character songs that have captured the hearts of anime fans worldwide!");
        
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "viewCount"));
        Page<Video> videosPage = dataSyncService.getVideoRepository().findAll(pageable);
        
        model.addAttribute("videos", videosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", videosPage.getTotalPages());
        model.addAttribute("totalElements", videosPage.getTotalElements());
        model.addAttribute("hasNext", videosPage.hasNext());
        model.addAttribute("hasPrevious", videosPage.hasPrevious());
        
        return "popular-videos";
    }

    /**
     * All videos page with search and filtering
     */
    @GetMapping("/all-videos")
    public String allVideos(Model model, 
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "publishedAt") String sortBy,
                           @RequestParam(defaultValue = "desc") String sortDir,
                           @RequestParam(required = false) String search) {
        addCommonAttributes(model);
        
        model.addAttribute("pageTitle", "All Videos");
        model.addAttribute("pageDescription", "Browse our complete collection of AI character songs from anime and manga!");
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, 12, Sort.by(direction, sortBy));
        
        Page<Video> videosPage;
        if (search != null && !search.trim().isEmpty()) {
            videosPage = dataSyncService.getVideoRepository().findByTitleContainingIgnoreCase(search.trim(), pageable);
            model.addAttribute("search", search);
        } else {
            videosPage = dataSyncService.getVideoRepository().findAll(pageable);
        }
        
        model.addAttribute("videos", videosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", videosPage.getTotalPages());
        model.addAttribute("totalElements", videosPage.getTotalElements());
        model.addAttribute("hasNext", videosPage.hasNext());
        model.addAttribute("hasPrevious", videosPage.hasPrevious());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        
        return "all-videos";
    }

    /**
     * Playlists page
     */
    @GetMapping("/playlists")
    public String playlists(Model model) {
        addCommonAttributes(model);
        
        model.addAttribute("pageTitle", "Playlists");
        model.addAttribute("pageDescription", "Explore our curated playlists featuring AI character songs organized by themes, anime series, and more!");
        
        List<Playlist> playlists = dataSyncService.getPlaylists();
        model.addAttribute("playlists", playlists);
        
        return "playlists";
    }

    /**
     * About page
     */
    @GetMapping("/about")
    public String about(Model model) {
        addCommonAttributes(model);
        
        model.addAttribute("pageTitle", "About Me");
        model.addAttribute("pageDescription", "Learn more about the creator of Random Videos 139.");
        
        // Creator information
        model.addAttribute("creatorName", "Justin");
        model.addAttribute("creatorTitle", "AI Music Creator");
        model.addAttribute("favoriteAnime", "Code Geass");
        model.addAttribute("favoriteManga", "One Piece");
        model.addAttribute("channelHeritage", "139 pays tribute to Attack on Titan Ch. 139");
        
        return "about";
    }

    /**
     * Add common attributes to all pages
     */
    private void addCommonAttributes(Model model) {
        model.addAttribute("channelName", channelName);
        model.addAttribute("channelUrl", channelUrl);
        model.addAttribute("facebookUrl", facebookUrl);
        model.addAttribute("instagramUrl", instagramUrl);
        model.addAttribute("twitterUrl", twitterUrl);
        model.addAttribute("redditUrl", redditUrl);
        model.addAttribute("tiktokUrl", tiktokUrl);
        model.addAttribute("tumblrUrl", tumblrUrl);
        
        // API connection status
        boolean apiConnected = dataSyncService.isApiConnected();
        model.addAttribute("apiConnected", apiConnected);
    }
}

