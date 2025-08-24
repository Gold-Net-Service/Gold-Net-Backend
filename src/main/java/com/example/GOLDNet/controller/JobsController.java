package com.example.GOLDNet.controller;

import com.example.GOLDNet.service.CrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JobsController {
    private final CrawlerService crawlerService;

    @GetMapping("/crawl-jobs")
    public List<Map<String, String>> crawlJobs() {
//        System.out.println(crawlerService.crawlJobs());
        return crawlerService.crawlJobs();
    }
}
