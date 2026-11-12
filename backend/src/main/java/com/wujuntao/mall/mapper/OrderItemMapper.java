package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrderItemMapper {

    @Insert("INSERT INTO order_item(order_id, product_id, product_name, price, quantity, cover) "
            + "VALUES(#{orderId}, #{productId}, #{productName}, #{price}, #{quantity}, #{cover})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem item);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
}
