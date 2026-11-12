package com.wujuntao.mall.controller;

import com.wujuntao.mall.common.PageResult;
import com.wujuntao.mall.common.Result;
import com.wujuntao.mall.entity.Category;
import com.wujuntao.mall.entity.Product;
import com.wujuntao.mall.service.ShopService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.ok(shopService.categories());
    }

    @GetMapping("/products")
    public Result<PageResult<Product>> products(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Long categoryId,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return Result.ok(shopService.search(keyword, categoryId, page, size, true));
    }

    @GetMapping("/products/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(shopService.detail(id));
    }
}
