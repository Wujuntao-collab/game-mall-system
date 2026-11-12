package com.wujuntao.mall.config;

import com.wujuntao.mall.common.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!"ADMIN".equals(request.getAttribute("role"))) {
            throw new BizException(403, "需要管理员权限");
        }
        return true;
    }
}
