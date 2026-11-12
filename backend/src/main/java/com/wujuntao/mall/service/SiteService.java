package com.wujuntao.mall.service;

import com.wujuntao.mall.common.BizException;
import com.wujuntao.mall.entity.Banner;
import com.wujuntao.mall.entity.Notice;
import com.wujuntao.mall.mapper.BannerMapper;
import com.wujuntao.mall.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteService {
    private final NoticeMapper noticeMapper;
    private final BannerMapper bannerMapper;

    public SiteService(NoticeMapper noticeMapper, BannerMapper bannerMapper) {
        this.noticeMapper = noticeMapper;
        this.bannerMapper = bannerMapper;
    }

    public List<Notice> notices() {
        return noticeMapper.listAll();
    }

    public List<Banner> banners() {
        return bannerMapper.listAll();
    }

    public Notice saveNotice(Notice notice) {
        if (notice.getTitle() == null || notice.getTitle().isBlank()) {
            throw new BizException("公告标题不能为空");
        }
        if (notice.getId() == null) {
            noticeMapper.insert(notice);
        } else {
            noticeMapper.update(notice);
        }
        return noticeMapper.findById(notice.getId());
    }

    public void deleteNotice(Long id) {
        noticeMapper.delete(id);
    }

    public Banner saveBanner(Banner banner) {
        if (banner.getImageUrl() == null || banner.getImageUrl().isBlank()) {
            throw new BizException("轮播图地址不能为空");
        }
        if (banner.getSort() == null) {
            banner.setSort(0);
        }
        if (banner.getId() == null) {
            bannerMapper.insert(banner);
        } else {
            bannerMapper.update(banner);
        }
        return banner;
    }

    public void deleteBanner(Long id) {
        bannerMapper.delete(id);
    }
}
