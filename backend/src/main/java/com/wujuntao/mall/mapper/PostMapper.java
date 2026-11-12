package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.Post;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface PostMapper {

    @Select("""
            <script>
            SELECT p.*, u.username, u.nickname, pr.name AS product_name,
                   (SELECT COUNT(*) FROM comment c WHERE c.post_id = p.id) AS comment_count
            FROM post p
            LEFT JOIN sys_user u ON u.id = p.user_id
            LEFT JOIN product pr ON pr.id = p.product_id
            <where>
              <if test="status != null">AND p.status = #{status}</if>
              <if test="keyword != null and keyword != ''">
                AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.content LIKE CONCAT('%', #{keyword}, '%'))
              </if>
            </where>
            ORDER BY p.id DESC
            LIMIT #{size} OFFSET #{offset}
            </script>
            """)
    List<Post> search(@Param("status") Integer status,
                      @Param("keyword") String keyword,
                      @Param("offset") int offset,
                      @Param("size") int size);

    @Select("""
            <script>
            SELECT COUNT(*) FROM post p
            <where>
              <if test="status != null">AND p.status = #{status}</if>
              <if test="keyword != null and keyword != ''">
                AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.content LIKE CONCAT('%', #{keyword}, '%'))
              </if>
            </where>
            </script>
            """)
    long count(@Param("status") Integer status, @Param("keyword") String keyword);

    @Select("SELECT p.*, u.username, u.nickname, pr.name AS product_name, "
            + "(SELECT COUNT(*) FROM comment c WHERE c.post_id = p.id) AS comment_count "
            + "FROM post p LEFT JOIN sys_user u ON u.id = p.user_id "
            + "LEFT JOIN product pr ON pr.id = p.product_id WHERE p.id = #{id}")
    Post findById(@Param("id") Long id);

    @Insert("INSERT INTO post(user_id, title, content, product_id, status) "
            + "VALUES(#{userId}, #{title}, #{content}, #{productId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Post post);

    @Update("UPDATE post SET views = views + 1 WHERE id = #{id}")
    int increaseViews(@Param("id") Long id);

    @Update("UPDATE post SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM post WHERE id = #{id}")
    int delete(@Param("id") Long id);
}
