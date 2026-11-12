package com.wujuntao.mall.controller;

import com.wujuntao.mall.common.PageResult;
import com.wujuntao.mall.common.Result;
import com.wujuntao.mall.dto.OrderCreateRequest;
import com.wujuntao.mall.entity.Order;
import com.wujuntao.mall.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<Order> create(@RequestAttribute("uid") Long uid, @RequestBody OrderCreateRequest request) {
        return Result.ok(orderService.create(uid, request));
    }

    @GetMapping
    public Result<PageResult<Order>> mine(@RequestAttribute("uid") Long uid,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return Result.ok(orderService.mine(uid, page, size));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@RequestAttribute("uid") Long uid, @PathVariable Long id) {
        return Result.ok(orderService.detail(uid, id));
    }

    @PostMapping("/{id}/pay")
    public Result<Void> pay(@RequestAttribute("uid") Long uid, @PathVariable Long id) {
        orderService.pay(uid, id);
        return Result.ok();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@RequestAttribute("uid") Long uid, @PathVariable Long id) {
        orderService.cancel(uid, id);
        return Result.ok();
    }

    @PostMapping("/{id}/confirm")
    public Result<Void> confirm(@RequestAttribute("uid") Long uid, @PathVariable Long id) {
        orderService.confirm(uid, id);
        return Result.ok();
    }
}
