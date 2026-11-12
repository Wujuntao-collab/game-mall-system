package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.Product;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ProductMapper {

    @Select("""
            <script>
            SELECT p.*, c.name AS category_name FROM product p
            LEFT JOIN category c ON c.id = p.category_id
            <where>
              <if test="keyword != null and keyword != ''">AND p.name LIKE CONCAT('%', #{keyword}, '%')</if>
              <if test="categoryId != null">AND p.category_id = #{categoryId}</if>
              <if test="onlyOnSale">AND p.status = 1</if>
            </where>
            ORDER BY p.id DESC
            LIMIT #{size} OFFSET #{offset}
            </script>
            """)
    List<Product> search(@Param("keyword") String keyword,
                         @Param("categoryId") Long categoryId,
                         @Param("onlyOnSale") boolean onlyOnSale,
                         @Param("offset") int offset,
                         @Param("size") int size);

    @Select("""
            <script>
            SELECT COUNT(*) FROM product p
            <where>
              <if test="keyword != null and keyword != ''">AND p.name LIKE CONCAT('%', #{keyword}, '%')</if>
              <if test="categoryId != null">AND p.category_id = #{categoryId}</if>
              <if test="onlyOnSale">AND p.status = 1</if>
            </where>
            </script>
            """)
    long count(@Param("keyword") String keyword,
               @Param("categoryId") Long categoryId,
               @Param("onlyOnSale") boolean onlyOnSale);

    @Select("SELECT p.*, c.name AS category_name FROM product p LEFT JOIN category c ON c.id = p.category_id "
            + "WHERE p.id = #{id}")
    Product findById(@Param("id") Long id);

    @Insert("INSERT INTO product(name, category_id, type, price, stock, cover, description, status) "
            + "VALUES(#{name}, #{categoryId}, #{type}, #{price}, #{stock}, #{cover}, #{description}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE product SET name = #{name}, category_id = #{categoryId}, type = #{type}, price = #{price}, "
            + "stock = #{stock}, cover = #{cover}, description = #{description}, status = #{status} WHERE id = #{id}")
    int update(Product product);

    @Update("UPDATE product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM product WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Update("UPDATE product SET stock = stock - #{qty} WHERE id = #{id} AND stock >= #{qty}")
    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);

    @Update("UPDATE product SET stock = stock + #{qty} WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("qty") int qty);

    @Select("SELECT COUNT(*) FROM product")
    long countAll();
}
