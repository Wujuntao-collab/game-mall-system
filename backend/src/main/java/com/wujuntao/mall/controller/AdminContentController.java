package com.wujuntao.mall.controller;

import com.wujuntao.mall.common.PageResult;
import com.wujuntao.mall.common.Result;
import com.wujuntao.mall.entity.Banner;
import com.wujuntao.mall.entity.Comment;
import com.wujuntao.mall.entity.Notice;
import com.wujuntao.mall.entity.Post;
import com.wujuntao.mall.service.CommunityService;
import com.wujuntao.mall.service.SiteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminContentController {
    private final CommunityService communityService;
    private final SiteService siteService;

    public AdminContentController(CommunityService communityService, SiteService siteService) {
        this.communityService = communityService;
        this.siteService = siteService;
    }

    @GetMapping("/posts")
    public Result<PageResult<Post>> posts(@RequestParam(required = false) Integer status,
                                          @RequestParam(required = false) String keyword,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return Result.ok(communityService.adminList(status, keyword, page, size));
    }

    @PutMapping("/posts/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        communityService.audit(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        communityService.audit(id, 2);
        return Result.ok();
    }

    @GetMapping("/posts/{id}/comments")
    public Result<List<Comment>> comments(@PathVariable Long id) {
        return Result.ok(communityService.comments(id));
    }

    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        communityService.deleteComment(id);
        return Result.ok();
    }

    @PostMapping("/notices")
    public Result<Notice> createNotice(@RequestBody Notice notice) {
        notice.setId(null);
        return Result.ok(siteService.saveNotice(notice));
    }

    @PutMapping("/notices/{id}")
    public Result<Notice> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        return Result.ok(siteService.saveNotice(notice));
    }

    @DeleteMapping("/notices/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        siteService.deleteNotice(id);
        return Result.ok();
    }

    @PostMapping("/banners")
    public Result<Banner> createBanner(@RequestBody Banner banner) {
        banner.setId(null);
        return Result.ok(siteService.saveBanner(banner));
    }

    @PutMapping("/banners/{id}")
    public Result<Banner> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        return Result.ok(siteService.saveBanner(banner));
    }

    @DeleteMapping("/banners/{id}")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        siteService.deleteBanner(id);
        return Result.ok();
    }
}
