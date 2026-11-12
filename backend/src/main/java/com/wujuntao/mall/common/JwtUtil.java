package com.wujuntao.mall.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 轻量 JWT 实现（HS256）。仅依赖 JDK，便于理解与答辩讲解。
 */
@Component
public class JwtUtil {
    private static final Pattern UID = Pattern.compile("\"uid\":(\\d+)");
    private static final Pattern ROLE = Pattern.compile("\"role\":\"([A-Z]+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\":(\\d+)");

    private final String secret;
    private final long expireSeconds;

    public JwtUtil(@Value("${mall.jwt.secret}") String secret,
                   @Value("${mall.jwt.expire-seconds}") long expireSeconds) {
        this.secret = secret;
        this.expireSeconds = expireSeconds;
    }

    public record TokenInfo(Long uid, String role) {
    }

    public String createToken(Long uid, String username, String role) {
        String header = b64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        long exp = System.currentTimeMillis() / 1000 + expireSeconds;
        String payload = b64("{\"uid\":" + uid + ",\"username\":\"" + safe(username)
                + "\",\"role\":\"" + safe(role) + "\",\"exp\":" + exp + "}");
        String sign = sign(header + "." + payload);
        return header + "." + payload + "." + sign;
    }

    public TokenInfo verify(String token) {
        if (token == null || token.isBlank()) {
            throw new BizException(401, "未登录");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new BizException(401, "token 格式不正确");
        }
        if (!sign(parts[0] + "." + parts[1]).equals(parts[2])) {
            throw new BizException(401, "token 签名校验失败");
        }
        String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        Matcher expMatcher = EXP.matcher(json);
        if (!expMatcher.find() || Long.parseLong(expMatcher.group(1)) < System.currentTimeMillis() / 1000) {
            throw new BizException(401, "登录已过期，请重新登录");
        }
        Matcher uidMatcher = UID.matcher(json);
        if (!uidMatcher.find()) {
            throw new BizException(401, "token 缺少用户信息");
        }
        Matcher roleMatcher = ROLE.matcher(json);
        String role = roleMatcher.find() ? roleMatcher.group(1) : "USER";
        return new TokenInfo(Long.parseLong(uidMatcher.group(1)), role);
    }

    private String safe(String text) {
        return text == null ? "" : text.replace("\"", "").replace("\\", "");
    }

    private String b64(String text) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new BizException(500, "token 生成失败");
        }
    }
}
