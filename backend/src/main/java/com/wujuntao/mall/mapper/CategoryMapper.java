package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CategoryMapper {

    @Select("SELECT * FROM category ORDER BY sort ASC, id ASC")
    List<Category> listAll();

    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(@Param("id") Long id);

    @Insert("INSERT INTO category(name, sort) VALUES(#{name}, #{sort})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    @Update("UPDATE category SET name = #{name}, sort = #{sort} WHERE id = #{id}")
    int update(Category category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM category")
    long count();
}
