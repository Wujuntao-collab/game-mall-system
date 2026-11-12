package com.wujuntao.mall.mapper;

import com.wujuntao.mall.entity.Banner;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface BannerMapper {

    @Select("SELECT * FROM banner ORDER BY sort ASC, id ASC")
    List<Banner> listAll();

    @Insert("INSERT INTO banner(image_url, link_url, sort) VALUES(#{imageUrl}, #{linkUrl}, #{sort})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Banner banner);

    @Update("UPDATE banner SET image_url = #{imageUrl}, link_url = #{linkUrl}, sort = #{sort} WHERE id = #{id}")
    int update(Banner banner);

    @Delete("DELETE FROM banner WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM banner")
    long count();
}
