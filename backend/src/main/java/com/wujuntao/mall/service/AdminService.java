package com.wujuntao.mall.service;

import com.wujuntao.mall.common.BizException;
import com.wujuntao.mall.common.PageResult;
import com.wujuntao.mall.entity.Order;
import com.wujuntao.mall.entity.Product;
import com.wujuntao.mall.entity.User;
import com.wujuntao.mall.mapper.OrderItemMapper;
import com.wujuntao.mall.mapper.OrderMapper;
import com.wujuntao.mall.mapper.ProductMapper;
import com.wujuntao.mall.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public AdminService(UserMapper userMapper, ProductMapper productMapper,
                        OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    public PageResult<User> users(int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 100);
        return new PageResult<>(userMapper.count(), userMapper.page((p - 1) * s, s));
    }

    public void updateUserStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    public Product saveProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new BizException("商品名称不能为空");
        }
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getType() == null) {
            product.setType("GAME");
        }
        if (product.getId() == null) {
            productMapper.insert(product);
        } else {
            productMapper.update(product);
        }
        return productMapper.findById(product.getId());
    }

    public void updateProductStatus(Long id, Integer status) {
        productMapper.updateStatus(id, status);
    }

    public void deleteProduct(Long id) {
        productMapper.delete(id);
    }

    public PageResult<Order> orders(int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 100);
        List<Order> list = orderMapper.pageAll((p - 1) * s, s);
        for (Order o : list) {
            o.setItems(orderItemMapper.findByOrderId(o.getId()));
        }
        return new PageResult<>(orderMapper.countAll(), list);
    }

    public void ship(Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BizException(404, "订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BizException("只有已支付订单可以发货");
        }
        orderMapper.updateStatus(orderId, 2, order.getPaidAt());
    }

    public Map<String, Object> stats() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userCount", userMapper.count());
        map.put("productCount", productMapper.countAll());
        map.put("orderCount", orderMapper.countAll());
        map.put("salesAmount", orderMapper.sumPaidAmount());
        return map;
    }
}
