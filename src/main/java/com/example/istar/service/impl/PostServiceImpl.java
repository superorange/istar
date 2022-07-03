package com.example.istar.service.impl;

import com.example.istar.dto.PageModel;
import com.example.istar.entity.Post;
import com.example.istar.handler.LoginUser;
import com.example.istar.mapper.PostMapper;
import com.example.istar.service.IPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {
    @Resource
    private PostMapper mapper;

    @Override
    public List<Post> querySelfPosts(PageModel pageModel) {
        String uuid = LoginUser.getCurrentUser().getUser().getUuid();
        return mapper.querySelfPosts(pageModel.getOffset(), pageModel.getCount(), uuid);
    }
}
