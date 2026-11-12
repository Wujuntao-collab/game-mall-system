package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderMapper {

    @Insert("INSERT INTO orders(order_no, user_id, total_amount, status, address) "
            + "VALUES(#{orderNo}, #{userId}, #{totalAmount}, #{status}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order findById(@Param("id") Long id);

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY id DESC LIMIT #{size} OFFSET #{offset}")
    List<Order> pageByUser(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId}")
    long countByUser(@Param("userId") Long userId);

    @Select("SELECT o.*, u.username FROM orders o LEFT JOIN sys_user u ON u.id = o.user_id "
            + "ORDER BY o.id DESC LIMIT #{size} OFFSET #{offset}")
    List<Order> pageAll(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM orders")
    long countAll();

    @Update("UPDATE orders SET status = #{status}, paid_at = #{paidAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("paidAt") LocalDateTime paidAt);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status IN (1, 2, 3)")
    BigDecimal sumPaidAmount();
}
