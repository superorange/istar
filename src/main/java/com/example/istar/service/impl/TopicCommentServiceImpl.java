package com.example.istar.service.impl;

import com.example.istar.entity.TopicCommentEntity;
import com.example.istar.mapper.TopicCommentMapper;
import com.example.istar.service.ITopicCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */
@Service
public class TopicCommentServiceImpl extends ServiceImpl<TopicCommentMapper, TopicCommentEntity> implements ITopicCommentService {

}
