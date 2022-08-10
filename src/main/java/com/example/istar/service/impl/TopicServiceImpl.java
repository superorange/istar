package com.example.istar.service.impl;

import com.example.istar.model.PageModel;
import com.example.istar.entity.TopicEntity;
import com.example.istar.handler.LoginUser;
import com.example.istar.mapper.TopicMapper;
import com.example.istar.service.ITopicService;
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
public class TopicServiceImpl extends ServiceImpl<TopicMapper, TopicEntity> implements ITopicService {

    public boolean updateOne(TopicEntity entity) {
        return getBaseMapper().updateByEntity(entity) == 1;
    }
}
