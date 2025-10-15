
package com.randomvideos139.website.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.randomvideos139.website.entity.ChannelStats;
import com.randomvideos139.website.entity.Video;
import com.randomvideos139.website.repository.VideoRepository;
import com.randomvideos139.website.service.DataSyncService;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSyncService dataSyncService;

    @MockBean
    private VideoRepository videoRepository;

    @BeforeEach
    public void setUp() {
        given(dataSyncService.getVideoRepository()).willReturn(videoRepository);
    }

    @Test
    public void testHomePage() throws Exception {
        // Mock data
        ChannelStats channelStats = new ChannelStats();
        channelStats.setSubscriberCount(1000L);
        channelStats.setVideoCount(100L);
        channelStats.setViewCount(100000L);
        given(dataSyncService.getChannelStats()).willReturn(Optional.of(channelStats));

        List<Video> featuredVideos = new ArrayList<>();
        featuredVideos.add(new Video());
        given(videoRepository.findAll(PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "publishedAt"))))
                .willReturn(new PageImpl<>(featuredVideos));

        List<Video> latestVideos = new ArrayList<>();
        latestVideos.add(new Video());
        given(videoRepository.findAll(PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "publishedAt"))))
                .willReturn(new PageImpl<>(latestVideos));

        List<Video> popularVideos = new ArrayList<>();
        popularVideos.add(new Video());
        given(videoRepository.findAll(PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "viewCount"))))
                .willReturn(new PageImpl<>(popularVideos));

        // Perform GET request and verify
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("featuredVideos"))
                .andExpect(model().attributeExists("latestVideos"))
                .andExpect(model().attributeExists("popularVideos"))
                .andExpect(model().attributeExists("channelStats"));
    }

    @Test
    public void testLatestVideosPage() throws Exception {
        // Mock data
        List<Video> videos = new ArrayList<>();
        videos.add(new Video());
        given(videoRepository.findAll(PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "publishedAt"))))
                .willReturn(new PageImpl<>(videos));

        // Perform GET request and verify
        mockMvc.perform(get("/latest-videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("latest-videos"))
                .andExpect(model().attributeExists("videos"));
    }

    @Test
    public void testPopularVideosPage() throws Exception {
        // Mock data
        List<Video> videos = new ArrayList<>();
        videos.add(new Video());
        given(videoRepository.findAll(PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "viewCount"))))
                .willReturn(new PageImpl<>(videos));

        // Perform GET request and verify
        mockMvc.perform(get("/popular-videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("popular-videos"))
                .andExpect(model().attributeExists("videos"));
    }

    @Test
    public void testAllVideosPage() throws Exception {
        // Mock data
        List<Video> videos = new ArrayList<>();
        videos.add(new Video());
        given(videoRepository.findAll(PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "publishedAt"))))
                .willReturn(new PageImpl<>(videos));

        // Perform GET request and verify
        mockMvc.perform(get("/all-videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-videos"))
                .andExpect(model().attributeExists("videos"));
    }

    @Test
    public void testAllVideosPageWithSearch() throws Exception {
        // Mock data
        List<Video> videos = new ArrayList<>();
        videos.add(new Video());
        given(videoRepository.findByTitleContainingIgnoreCase("test", PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "publishedAt"))))
                .willReturn(new PageImpl<>(videos));

        // Perform GET request and verify
        mockMvc.perform(get("/all-videos").param("search", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("all-videos"))
                .andExpect(model().attributeExists("videos"))
                .andExpect(model().attribute("search", "test"));
    }
}
