package com.example.istar.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.istar.common.Roles;
import com.example.istar.dto.PageModel;
import com.example.istar.dto.PostSimpleModel;
import com.example.istar.dto.PostWrapper;
import com.example.istar.entity.Pictures;
import com.example.istar.entity.Post;
import com.example.istar.entity.Videos;
import com.example.istar.handler.LoginUser;
import com.example.istar.model.PostModel;
import com.example.istar.service.impl.PicturesServiceImpl;
import com.example.istar.service.impl.PostServiceImpl;
import com.example.istar.service.impl.VideosServiceImpl;
import com.example.istar.utils.Exp;
import com.example.istar.utils.R;
import com.example.istar.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author tian
 * @since 2022-07-03
 */

@Api(tags = "帖子接口")
@RequestMapping("/posts")
@RestController
public class PostController {
    @Resource
    private PostServiceImpl postService;
    @Resource
    private PicturesServiceImpl picturesService;
    @Resource
    private VideosServiceImpl videosService;

    @PostMapping("")
    @ApiOperation(value = "添加帖子")
    public R<PostSimpleModel> addPost(@RequestBody PostModel model) throws Exp {
        if (!model.isCorrect()) {
            throw Exp.from(ResultCode.ERROR_PARAM);
        }
        LoginUser loginUser = LoginUser.getCurrentUser();
        Post post = new Post();
        post.setUuid(loginUser.getUser().getUuid());
        post.setPostId(DateUtil.format(new Date(), "yyyyMMddHHmmss") + "-" + UUID.fastUUID());
        post.setTitle(model.getTitle());
        post.setContent(model.getContent());
        post.setStatus(0);
        post.setGmtCreate(System.currentTimeMillis());
        post.setGmtModified(System.currentTimeMillis());
        //插入图片
        if (model.getPictures() != null && model.getPictures().size() > 0) {
            post.setPicId(String.join(";", model.getPictures()));
        }
        //插入视频
        if (model.getVideos() != null && model.getVideos().size() > 0) {
            post.setVideoId(String.join(";", model.getVideos()));
        }
        boolean save = postService.save(post);
        return save ? R.ok(new PostSimpleModel(post.getPostId())) : R.fail();
    }


    @GetMapping("/{postId}")
    @ApiOperation(value = "获取帖子")
    public R<PostWrapper> getPost(@PathVariable("postId") String postId) {
        LambdaQueryWrapper<Post> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //排除不可见的帖子
        lambdaQueryWrapper.eq(Post::getPostId, postId).eq(Post::getStatus, 0);
        Post post = postService.getOne(lambdaQueryWrapper);
        if (post != null) {
            PostWrapper postWrapper = new PostWrapper();
            BeanUtils.copyProperties(post, postWrapper);
            ///查询图片信息
            expandWrapper(postWrapper);
            return R.ok(postWrapper);
        }
        return R.ok();
    }

    @GetMapping("")
    @ApiOperation(value = "获取自己的所有帖子")
    public R<List<PostWrapper>> getPosts(PageModel model) {
        if (model == null) {
            model = new PageModel();
        }
        List<Post> posts = postService.querySelfPosts(model);
        List<PostWrapper> wrappers = posts.stream().map(post -> {
            PostWrapper postWrapper = new PostWrapper();
            BeanUtils.copyProperties(post, postWrapper);
            return postWrapper;
        }).collect(Collectors.toList());
        for (PostWrapper postWrapper : wrappers) {
            expandWrapper(postWrapper);
        }
        return R.ok(wrappers);
    }

    @DeleteMapping("/{postId}")
    @ApiOperation(value = "删除帖子")
    public R<Boolean> deletePost(@PathVariable("postId") String postId) {
        LoginUser user = LoginUser.getCurrentUser();
        LambdaQueryWrapper<Post> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Post::getPostId, postId);
        Post post = postService.getOne(lambdaQueryWrapper);
        if (post != null) {
            //只有是自己的帖子才可以删除
            if (user.getUser().getUuid().equals(post.getUuid())) {
                //是0才删除
                if (post.getStatus() == 0) {
                    post.setStatus(1);
                    boolean save = postService.updateById(post);
                    return save ? R.ok(true) : R.fail();
                }
                //-1禁止操作
                else if (post.getStatus() == -1) {
                    return R.fail(ResultCode.OPERATION_FAILED);
                }
                //1直接返回
                return R.ok(true);
            }
            //超级管理员可以删除,设置状态为-2
            else if (user.getUser().getRoles().equals(Roles.SYS_SUPER_ADMIN)) {
                post.setStatus(-2);
                boolean save = postService.updateById(post);
                return save ? R.ok(true) : R.fail();
            }
            return R.fail(ResultCode.OPERATION_FORBIDDEN);
        }
        return R.ok(true);
    }


    /**
     * 查询图片和视频信息
     *
     * @param postWrapper 帖子信息
     */

    private void expandWrapper(PostWrapper postWrapper) {
        ///查询图片信息
        if (ObjectUtil.isNotEmpty(postWrapper.getPicId())) {
            LambdaQueryWrapper<Pictures> picturesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            picturesLambdaQueryWrapper.in(Pictures::getPicId, Arrays.asList(postWrapper.getPicId().split(";")));
            List<Pictures> collect = picturesService.list(picturesLambdaQueryWrapper).stream().filter(pictures -> {
                ///去除违规图片
                return pictures.getStatus() == 0;
            }).collect(Collectors.toList());

            postWrapper.setPictures(collect);
        }
        ///查询视频信息
        if (ObjectUtil.isNotEmpty(postWrapper.getVideoId())) {
            LambdaQueryWrapper<Videos> videosLambdaQueryWrapper = new LambdaQueryWrapper<>();
            videosLambdaQueryWrapper.in(Videos::getVideoId, Arrays.asList(postWrapper.getVideoId().split(";")));
            List<Videos> collect = videosService.list(videosLambdaQueryWrapper).stream().filter(videos -> {
                ///去除违规图片
                return videos.getStatus() == 0;
            }).collect(Collectors.toList());
            postWrapper.setVideos(collect);
        }
    }

}
