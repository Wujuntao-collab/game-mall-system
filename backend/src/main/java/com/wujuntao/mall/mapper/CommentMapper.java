package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommentMapper {

    @Select("SELECT c.*, u.username, u.nickname FROM comment c "
            + "LEFT JOIN sys_user u ON u.id = c.user_id "
            + "WHERE c.post_id = #{postId} ORDER BY c.id ASC")
    List<Comment> listByPost(@Param("postId") Long postId);

    @Insert("INSERT INTO comment(post_id, user_id, content) VALUES(#{postId}, #{userId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Select("SELECT * FROM comment WHERE id = #{id}")
    Comment findById(@Param("id") Long id);

    @Delete("DELETE FROM comment WHERE id = #{id}")
    int delete(@Param("id") Long id);
}
