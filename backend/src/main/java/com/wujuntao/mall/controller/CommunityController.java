package com.wujuntao.mall.controller;

import com.wujuntao.mall.common.PageResult;
import com.wujuntao.mall.common.Result;
import com.wujuntao.mall.dto.CommentCreateRequest;
import com.wujuntao.mall.dto.PostCreateRequest;
import com.wujuntao.mall.entity.Comment;
import com.wujuntao.mall.entity.Post;
import com.wujuntao.mall.service.CommunityService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class CommunityController {
    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    public Result<PageResult<Post>> list(@RequestParam(required = false) String keyword,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return Result.ok(communityService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public Result<Post> detail(@PathVariable Long id) {
        return Result.ok(communityService.detail(id));
    }

    @PostMapping
    public Result<Post> create(@RequestAttribute("uid") Long uid, @RequestBody PostCreateRequest request) {
        return Result.ok(communityService.create(uid, request));
    }

    @PostMapping("/{id}/comments")
    public Result<Comment> comment(@RequestAttribute("uid") Long uid,
                                   @PathVariable Long id,
                                   @RequestBody CommentCreateRequest request) {
        return Result.ok(communityService.addComment(uid, id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestAttribute("uid") Long uid,
                               @RequestAttribute("role") String role,
                               @PathVariable Long id) {
        communityService.deletePost(uid, role, id);
        return Result.ok();
    }
}
