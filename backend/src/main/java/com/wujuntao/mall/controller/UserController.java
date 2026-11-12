package com.wujuntao.mall.controller;

import com.wujuntao.mall.common.BizException;
import com.wujuntao.mall.common.Result;
import com.wujuntao.mall.entity.User;
import com.wujuntao.mall.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public Result<User> me(@RequestAttribute("uid") Long uid) {
        User user = userMapper.findById(uid);
        if (user == null) {
            throw new BizException(404, "用户不存在");
        }
        return Result.ok(user);
    }
}
