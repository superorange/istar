package com.example.istar.service.impl;

import com.example.istar.entity.CommentEntity;
import com.example.istar.mapper.CommentMapper;
import com.example.istar.service.ICommentService;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements ICommentService {

}
