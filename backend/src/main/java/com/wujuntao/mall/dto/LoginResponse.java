package com.wujuntao.mall.dto;

public record LoginResponse(String token, Long id, String username, String nickname, String role) {
}
