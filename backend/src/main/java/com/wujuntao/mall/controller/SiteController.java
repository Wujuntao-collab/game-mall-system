package com.wujuntao.mall.controller;

import com.wujuntao.mall.common.Result;
import com.wujuntao.mall.entity.Banner;
import com.wujuntao.mall.entity.Notice;
import com.wujuntao.mall.service.SiteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SiteController {
    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/notices")
    public Result<List<Notice>> notices() {
        return Result.ok(siteService.notices());
    }

    @GetMapping("/banners")
    public Result<List<Banner>> banners() {
        return Result.ok(siteService.banners());
    }
}
