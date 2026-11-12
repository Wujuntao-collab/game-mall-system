package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.CartItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CartMapper {

    @Select("SELECT c.id, c.user_id, c.product_id, c.quantity, p.name AS product_name, p.price, p.cover, "
            + "p.stock, p.status AS product_status "
            + "FROM cart_item c JOIN product p ON p.id = c.product_id "
            + "WHERE c.user_id = #{userId} ORDER BY c.id DESC")
    List<CartItem> listByUser(@Param("userId") Long userId);

    @Select("SELECT * FROM cart_item WHERE user_id = #{userId} AND product_id = #{productId}")
    CartItem findByUserAndProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT * FROM cart_item WHERE id = #{id}")
    CartItem findById(@Param("id") Long id);

    @Insert("INSERT INTO cart_item(user_id, product_id, quantity) VALUES(#{userId}, #{productId}, #{quantity})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItem item);

    @Update("UPDATE cart_item SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("quantity") int quantity);

    @Delete("DELETE FROM cart_item WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);
}
