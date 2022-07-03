package com.example.istar.service;

import com.example.istar.dto.PageModel;
import com.example.istar.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
public interface IPostService extends IService<Post> {
    List<Post> querySelfPosts(PageModel pageModel);

}
