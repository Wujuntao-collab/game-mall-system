package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserMapper {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Insert("INSERT INTO sys_user(username, password, nickname, role, status) "
            + "VALUES(#{username}, #{password}, #{nickname}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT COUNT(*) FROM sys_user")
    long count();

    @Select("SELECT id, username, nickname, role, status, created_at FROM sys_user "
            + "ORDER BY id DESC LIMIT #{size} OFFSET #{offset}")
    List<User> page(@Param("offset") int offset, @Param("size") int size);

    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
