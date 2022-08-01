package com.example.istar.service;

import com.example.istar.model.PageModel;
import com.example.istar.entity.TopicEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
public interface ITopicService extends IService<TopicEntity> {
    List<TopicEntity> querySelfPosts(PageModel pageModel);
}
