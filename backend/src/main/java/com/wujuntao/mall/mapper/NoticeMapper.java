package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.Notice;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface NoticeMapper {

    @Select("SELECT * FROM notice ORDER BY id DESC")
    List<Notice> listAll();

    @Select("SELECT * FROM notice WHERE id = #{id}")
    Notice findById(@Param("id") Long id);

    @Insert("INSERT INTO notice(title, content) VALUES(#{title}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notice notice);

    @Update("UPDATE notice SET title = #{title}, content = #{content} WHERE id = #{id}")
    int update(Notice notice);

    @Delete("DELETE FROM notice WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM notice")
    long count();
}
