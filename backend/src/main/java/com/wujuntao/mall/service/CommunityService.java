package com.wujuntao.mall.service;

import com.wujuntao.mall.common.BizException;
import com.wujuntao.mall.common.PageResult;
import com.wujuntao.mall.dto.CommentCreateRequest;
import com.wujuntao.mall.dto.PostCreateRequest;
import com.wujuntao.mall.entity.Comment;
import com.wujuntao.mall.entity.Post;
import com.wujuntao.mall.mapper.CommentMapper;
import com.wujuntao.mall.mapper.PostMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityService {
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public CommunityService(PostMapper postMapper, CommentMapper commentMapper) {
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
    }

    public PageResult<Post> list(String keyword, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 50);
        long total = postMapper.count(1, keyword);
        List<Post> list = postMapper.search(1, keyword, (p - 1) * s, s);
        return new PageResult<>(total, list);
    }

    public Post detail(Long id) {
        Post post = postMapper.findById(id);
        if (post == null) {
            throw new BizException(404, "帖子不存在");
        }
        postMapper.increaseViews(id);
        post.setComments(commentMapper.listByPost(id));
        return post;
    }

    public Post create(Long userId, PostCreateRequest request) {
        if (request.title() == null || request.title().isBlank()) {
            throw new BizException("标题不能为空");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new BizException("内容不能为空");
        }
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.title());
        post.setContent(request.content());
        post.setProductId(request.productId());
        post.setStatus(0);
        postMapper.insert(post);
        return postMapper.findById(post.getId());
    }

    public Comment addComment(Long userId, Long postId, CommentCreateRequest request) {
        Post post = postMapper.findById(postId);
        if (post == null || post.getStatus() == null || post.getStatus() != 1) {
            throw new BizException(404, "帖子不存在或未通过审核");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new BizException("评论内容不能为空");
        }
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.content());
        commentMapper.insert(comment);
        return comment;
    }

    public void deletePost(Long userId, String role, Long postId) {
        Post post = postMapper.findById(postId);
        if (post == null) {
            throw new BizException(404, "帖子不存在");
        }
        boolean owner = post.getUserId() != null && post.getUserId().equals(userId);
        if (!owner && !"ADMIN".equals(role)) {
            throw new BizException(403, "只能删除自己的帖子");
        }
        postMapper.delete(postId);
    }

    // ---------------- 管理端 ----------------

    public PageResult<Post> adminList(Integer status, String keyword, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 100);
        long total = postMapper.count(status, keyword);
        List<Post> list = postMapper.search(status, keyword, (p - 1) * s, s);
        return new PageResult<>(total, list);
    }

    public void audit(Long postId, Integer status) {
        if (status == null || (status != 0 && status != 1 && status != 2)) {
            throw new BizException("审核状态只能是 0 待审 / 1 通过 / 2 驳回");
        }
        if (postMapper.findById(postId) == null) {
            throw new BizException(404, "帖子不存在");
        }
        postMapper.updateStatus(postId, status);
    }

    public void deleteComment(Long commentId) {
        commentMapper.delete(commentId);
    }

    public List<Comment> comments(Long postId) {
        return commentMapper.listByPost(postId);
    }
}
