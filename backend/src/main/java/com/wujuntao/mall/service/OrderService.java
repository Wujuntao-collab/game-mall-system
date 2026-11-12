package com.wujuntao.mall.service;

import com.wujuntao.mall.common.BizException;
import com.wujuntao.mall.common.PageResult;
import com.wujuntao.mall.dto.OrderCreateRequest;
import com.wujuntao.mall.dto.OrderItemRequest;
import com.wujuntao.mall.entity.CartItem;
import com.wujuntao.mall.entity.Order;
import com.wujuntao.mall.entity.OrderItem;
import com.wujuntao.mall.entity.Product;
import com.wujuntao.mall.mapper.CartMapper;
import com.wujuntao.mall.mapper.OrderItemMapper;
import com.wujuntao.mall.mapper.OrderMapper;
import com.wujuntao.mall.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {
    private static final DateTimeFormatter NO_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final CartMapper cartMapper;

    public OrderService(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                        ProductMapper productMapper, CartMapper cartMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.cartMapper = cartMapper;
    }

    @Transactional
    public Order create(Long userId, OrderCreateRequest request) {
        List<OrderItemRequest> source = request.items();
        boolean fromCart = source == null || source.isEmpty();
        if (fromCart) {
            List<CartItem> cartItems = cartMapper.listByUser(userId);
            if (cartItems.isEmpty()) {
                throw new BizException("购物车为空");
            }
            source = new ArrayList<>();
            for (CartItem c : cartItems) {
                source.add(new OrderItemRequest(c.getProductId(), c.getQuantity()));
            }
        }

        // 先校验并计算总额，再落库，避免出现"订单已生成但总额为 0"的中间态
        List<Product> products = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest req : source) {
            int qty = req.quantity() == null || req.quantity() < 1 ? 1 : req.quantity();
            Product product = productMapper.findById(req.productId());
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw new BizException("商品不存在或已下架：" + req.productId());
            }
            if (product.getStock() == null || product.getStock() < qty) {
                throw new BizException("库存不足：" + product.getName());
            }
            products.add(product);
            quantities.add(qty);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        Order order = new Order();
        order.setOrderNo("GM" + LocalDateTime.now().format(NO_FORMAT)
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000)));
        order.setUserId(userId);
        order.setStatus(0);
        order.setAddress(request.address());
        order.setTotalAmount(total);
        orderMapper.insert(order);

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int qty = quantities.get(i);
            // 条件更新扣库存：stock >= qty 才会成功，天然防超卖
            if (productMapper.decreaseStock(product.getId(), qty) == 0) {
                throw new BizException("库存不足：" + product.getName());
            }
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setPrice(product.getPrice());
            item.setQuantity(qty);
            item.setCover(product.getCover());
            orderItemMapper.insert(item);
        }

        Order saved = orderMapper.findById(order.getId());
        saved.setItems(orderItemMapper.findByOrderId(order.getId()));

        if (fromCart) {
            for (CartItem c : cartMapper.listByUser(userId)) {
                cartMapper.delete(c.getId(), userId);
            }
        }
        return saved;
    }

    public PageResult<Order> mine(Long userId, int page, int size) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 50);
        long total = orderMapper.countByUser(userId);
        List<Order> list = orderMapper.pageByUser(userId, (p - 1) * s, s);
        for (Order o : list) {
            o.setItems(orderItemMapper.findByOrderId(o.getId()));
        }
        return new PageResult<>(total, list);
    }

    public Order detail(Long userId, Long orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BizException(404, "订单不存在");
        }
        order.setItems(orderItemMapper.findByOrderId(orderId));
        return order;
    }

    public void pay(Long userId, Long orderId) {
        Order order = detail(userId, orderId);
        if (order.getStatus() != 0) {
            throw new BizException("当前状态不能支付");
        }
        orderMapper.updateStatus(orderId, 1, LocalDateTime.now());
    }

    @Transactional
    public void cancel(Long userId, Long orderId) {
        Order order = detail(userId, orderId);
        if (order.getStatus() != 0) {
            throw new BizException("只有待支付订单可以取消");
        }
        for (OrderItem item : order.getItems()) {
            productMapper.increaseStock(item.getProductId(), item.getQuantity());
        }
        orderMapper.updateStatus(orderId, 4, null);
    }

    public void confirm(Long userId, Long orderId) {
        Order order = detail(userId, orderId);
        if (order.getStatus() != 2) {
            throw new BizException("只有已发货订单可以确认收货");
        }
        orderMapper.updateStatus(orderId, 3, order.getPaidAt());
    }
}
