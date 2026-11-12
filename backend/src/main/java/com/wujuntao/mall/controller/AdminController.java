package com.wujuntao.mall.controller;

import com.wujuntao.mall.common.PageResult;
import com.wujuntao.mall.common.Result;
import com.wujuntao.mall.entity.Order;
import com.wujuntao.mall.entity.Product;
import com.wujuntao.mall.entity.User;
import com.wujuntao.mall.service.AdminService;
import com.wujuntao.mall.service.ShopService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final ShopService shopService;

    public AdminController(AdminService adminService, ShopService shopService) {
        this.adminService = adminService;
        this.shopService = shopService;
    }

    @GetMapping("/users")
    public Result<PageResult<User>> users(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return Result.ok(adminService.users(page, size));
    }

    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        adminService.updateUserStatus(id, body.get("status"));
        return Result.ok();
    }

    @GetMapping("/products")
    public Result<PageResult<Product>> products(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) Long categoryId,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return Result.ok(shopService.search(keyword, categoryId, page, size, false));
    }

    @PostMapping("/products")
    public Result<Product> createProduct(@RequestBody Product product) {
        product.setId(null);
        return Result.ok(adminService.saveProduct(product));
    }

    @PutMapping("/products/{id}")
    public Result<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return Result.ok(adminService.saveProduct(product));
    }

    @PutMapping("/products/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        adminService.updateProductStatus(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/products/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return Result.ok();
    }

    @GetMapping("/orders")
    public Result<PageResult<Order>> orders(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(adminService.orders(page, size));
    }

    @PutMapping("/orders/{id}/ship")
    public Result<Void> ship(@PathVariable Long id) {
        adminService.ship(id);
        return Result.ok();
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.ok(adminService.stats());
    }
}
