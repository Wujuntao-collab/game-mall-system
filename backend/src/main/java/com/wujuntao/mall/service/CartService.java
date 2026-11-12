package com.wujuntao.mall.service;

import com.wujuntao.mall.common.BizException;
import com.wujuntao.mall.entity.CartItem;
import com.wujuntao.mall.entity.Product;
import com.wujuntao.mall.mapper.CartMapper;
import com.wujuntao.mall.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    public CartService(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    public List<CartItem> list(Long userId) {
        return cartMapper.listByUser(userId);
    }

    public void add(Long userId, Long productId, Integer quantity) {
        int qty = quantity == null || quantity < 1 ? 1 : quantity;
        Product product = productMapper.findById(productId);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BizException("商品不存在或已下架");
        }
        if (product.getStock() == null || product.getStock() < qty) {
            throw new BizException("库存不足");
        }
        CartItem exist = cartMapper.findByUserAndProduct(userId, productId);
        if (exist == null) {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(qty);
            cartMapper.insert(item);
        } else {
            int newQty = exist.getQuantity() + qty;
            if (newQty > product.getStock()) {
                throw new BizException("库存不足");
            }
            cartMapper.updateQuantity(exist.getId(), newQty);
        }
    }

    public void updateQuantity(Long userId, Long id, Integer quantity) {
        CartItem item = cartMapper.findById(id);
        if (item == null || !item.getUserId().equals(userId)) {
            throw new BizException(404, "购物车项不存在");
        }
        int qty = quantity == null || quantity < 1 ? 1 : quantity;
        Product product = productMapper.findById(item.getProductId());
        if (product != null && product.getStock() != null && qty > product.getStock()) {
            throw new BizException("库存不足");
        }
        cartMapper.updateQuantity(id, qty);
    }

    public void remove(Long userId, Long id) {
        cartMapper.delete(id, userId);
    }
}
