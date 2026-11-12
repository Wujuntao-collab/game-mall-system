package com.wujuntao.mall.controller;

import com.wujuntao.mall.common.Result;
import com.wujuntao.mall.dto.CartAddRequest;
import com.wujuntao.mall.entity.CartItem;
import com.wujuntao.mall.service.CartService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Result<List<CartItem>> list(@RequestAttribute("uid") Long uid) {
        return Result.ok(cartService.list(uid));
    }

    @PostMapping
    public Result<Void> add(@RequestAttribute("uid") Long uid, @RequestBody CartAddRequest request) {
        cartService.add(uid, request.productId(), request.quantity());
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@RequestAttribute("uid") Long uid,
                               @PathVariable Long id,
                               @RequestBody Map<String, Integer> body) {
        cartService.updateQuantity(uid, id, body.get("quantity"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestAttribute("uid") Long uid, @PathVariable Long id) {
        cartService.remove(uid, id);
        return Result.ok();
    }
}
