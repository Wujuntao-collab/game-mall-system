package com.wujuntao.mall.config;

import com.wujuntao.mall.entity.Banner;
import com.wujuntao.mall.entity.Category;
import com.wujuntao.mall.entity.Comment;
import com.wujuntao.mall.entity.Notice;
import com.wujuntao.mall.entity.Post;
import com.wujuntao.mall.entity.Product;
import com.wujuntao.mall.entity.User;
import com.wujuntao.mall.mapper.BannerMapper;
import com.wujuntao.mall.mapper.CategoryMapper;
import com.wujuntao.mall.mapper.CommentMapper;
import com.wujuntao.mall.mapper.NoticeMapper;
import com.wujuntao.mall.mapper.PostMapper;
import com.wujuntao.mall.mapper.ProductMapper;
import com.wujuntao.mall.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 首次启动时写入管理员账号与演示数据，方便直接演示。
 */
@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final NoticeMapper noticeMapper;
    private final BannerMapper bannerMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, CategoryMapper categoryMapper, ProductMapper productMapper,
                           PostMapper postMapper, CommentMapper commentMapper, NoticeMapper noticeMapper,
                           BannerMapper bannerMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.noticeMapper = noticeMapper;
        this.bannerMapper = bannerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = userMapper.findByUsername("admin");
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("管理员");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            userMapper.insert(admin);
            log.info("已初始化管理员账号 admin / admin123");
        }

        if (categoryMapper.count() == 0) {
            categoryMapper.insert(category("数字版游戏", 1));
            categoryMapper.insert(category("虚拟道具", 2));
            categoryMapper.insert(category("实体周边", 3));
        }

        if (productMapper.countAll() == 0) {
            List<Category> categories = categoryMapper.listAll();
            Long game = categories.get(0).getId();
            Long item = categories.get(1).getId();
            Long goods = categories.get(2).getId();
            productMapper.insert(product("星海远征 数字标准版", game, "GAME",
                    "128.00", 100, "太空题材开放世界 RPG，含全部主线剧情。"));
            productMapper.insert(product("暗影之城 数字豪华版", game, "GAME",
                    "268.00", 80, "含豪华皮肤包与剧情 DLC。"));
            productMapper.insert(product("限定坐骑·夜刃", item, "ITEM",
                    "68.00", 200, "账号绑定，购买后自动发放到游戏内邮箱。"));
            productMapper.insert(product("传说武器礼包", item, "ITEM",
                    "198.00", 60, "含随机传说武器 x3，道具类商品不支持退款。"));
            productMapper.insert(product("星海远征 官方设定集", goods, "GOODS",
                    "158.00", 40, "精装 240 页，下单后 48 小时内发货。"));
            productMapper.insert(product("游戏主题鼠标垫", goods, "GOODS",
                    "49.00", 120, "900x400mm 加厚桌垫，实拍图见详情。"));
        }

        if (noticeMapper.count() == 0) {
            noticeMapper.insert(notice("平台上线公告", "游戏商城 v1.0 正式上线，支持数字商品即时发放与实物周边发货。"));
            noticeMapper.insert(notice("新用户优惠", "注册即送 10 元无门槛券，有效期 30 天。"));
        }

        if (bannerMapper.count() == 0) {
            bannerMapper.insert(banner("https://picsum.photos/seed/mall-a/1200/360", "/products", 1));
            bannerMapper.insert(banner("https://picsum.photos/seed/mall-b/1200/360", "/posts", 2));
        }

        if (postMapper.count(null, null) == 0) {
            List<Product> products = productMapper.search(null, null, true, 0, 1);
            Long productId = products.isEmpty() ? null : products.get(0).getId();

            Post post = new Post();
            post.setUserId(admin.getId());
            post.setTitle("《星海远征》新手开荒指南");
            post.setContent("前期优先推主线拿飞船改装件，资源点集中在第三星系。"
                    + "氪金点主要在皮肤，纯数字版即可体验全部剧情，性价比最高。");
            post.setProductId(productId);
            post.setStatus(1);
            postMapper.insert(post);

            Comment comment = new Comment();
            comment.setPostId(post.getId());
            comment.setUserId(admin.getId());
            comment.setContent("补充：每周三的限时活动掉落翻倍，建议那时候集中刷。");
            commentMapper.insert(comment);
        }
    }

    private Category category(String name, int sort) {
        Category c = new Category();
        c.setName(name);
        c.setSort(sort);
        return c;
    }

    private Product product(String name, Long categoryId, String type, String price, int stock, String desc) {
        Product p = new Product();
        p.setName(name);
        p.setCategoryId(categoryId);
        p.setType(type);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        p.setDescription(desc);
        p.setStatus(1);
        p.setCover("");
        return p;
    }

    private Notice notice(String title, String content) {
        Notice n = new Notice();
        n.setTitle(title);
        n.setContent(content);
        return n;
    }

    private Banner banner(String imageUrl, String linkUrl, int sort) {
        Banner b = new Banner();
        b.setImageUrl(imageUrl);
        b.setLinkUrl(linkUrl);
        b.setSort(sort);
        return b;
    }
}
