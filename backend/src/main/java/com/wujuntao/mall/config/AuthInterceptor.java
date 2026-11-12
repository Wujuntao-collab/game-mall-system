package com.wujuntao.mall.config;

import com.wujuntao.mall.common.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 商城、社区、公告、轮播属于公开只读内容，允许匿名 GET
        if ("GET".equalsIgnoreCase(request.getMethod()) && isPublicRead(request.getRequestURI())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        String token = (header != null && header.startsWith("Bearer ")) ? header.substring(7) : header;
        JwtUtil.TokenInfo info = jwtUtil.verify(token);
        request.setAttribute("uid", info.uid());
        request.setAttribute("role", info.role());
        return true;
    }

    private boolean isPublicRead(String uri) {
        return uri.startsWith("/api/products")
                || uri.startsWith("/api/categories")
                || uri.startsWith("/api/posts")
                || uri.startsWith("/api/notices")
                || uri.startsWith("/api/banners");
    }
}
