package com.wujuntao.mall.service;

import com.wujuntao.mall.common.BizException;
import com.wujuntao.mall.common.PageResult;
import com.wujuntao.mall.entity.Category;
import com.wujuntao.mall.entity.Product;
import com.wujuntao.mall.mapper.CategoryMapper;
import com.wujuntao.mall.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    public ShopService(ProductMapper productMapper, CategoryMapper categoryMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    public List<Category> categories() {
        return categoryMapper.listAll();
    }

    public PageResult<Product> search(String keyword, Long categoryId, int page, int size, boolean onlyOnSale) {
        int p = Math.max(page, 1);
        int s = Math.min(Math.max(size, 1), 100);
        long total = productMapper.count(keyword, categoryId, onlyOnSale);
        List<Product> list = productMapper.search(keyword, categoryId, onlyOnSale, (p - 1) * s, s);
        return new PageResult<>(total, list);
    }

    public Product detail(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BizException(404, "商品不存在");
        }
        return product;
    }
}
