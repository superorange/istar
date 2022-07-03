package com.example.istar.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cn.hutool.core.util.RandomUtil;
import com.example.istar.common.Code;
import com.example.istar.common.RedisConst;
import com.example.istar.dto.CodeModel;
import com.example.istar.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "验证码接口")
@RequestMapping("/code")
public class CodeController {
    @Resource
    private RedisCache redisCache;
    @Resource
    private ImageCaptchaApplication imageCaptchaApplication;

    @ApiOperation(value = "发送验证码", notes = "用户发送验证码")
    @PostMapping("/send")
    public R sendCode(@RequestBody CodeModel model) throws Exp {
        if (model.isBadFormat()) {
            throw Exp.from(ResultCode.ERROR_PARAM);
        }
        ///TODO 获取之前应该校验一下
        switch (model.getTag()) {
            case RedisConst.LOGIN:
                return sendLoginCode(model);
            default:
                return R.fail("未知的标签");
        }
    }

    @ApiOperation(value = "生成滑块验证", notes = "用户生成滑块验证")
    @PostMapping("/gen")
    public R<CaptchaResponse<ImageCaptchaVO>> genCode() {
        CaptchaResponse<ImageCaptchaVO> res1 = imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
//        CaptchaResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(CaptchaImageType.WEBP);
        return R.ok(res1);
    }

    @PostMapping("/check")
    @ResponseBody
    public R checkCaptcha(@RequestParam("id") String id,
                          @RequestBody ImageCaptchaTrack imageCaptchaTrack) {
//        ImageCaptchaTrack track = new ImageCaptchaTrack();
//        track.setBgImageHeight(imageCaptchaTrack.getBgImageHeight());
//        track.setBgImageWidth(imageCaptchaTrack.getBgImageWidth());
//        track.setSliderImageHeight(imageCaptchaTrack.getSliderImageHeight());
//        track.setSliderImageWidth(imageCaptchaTrack.getSliderImageWidth());
//        track.setStartSlidingTime(imageCaptchaTrack.getStartSlidingTime());
//        track.setEndSlidingTime(imageCaptchaTrack.getEndSlidingTime());
//        List<ImageCaptchaTrack.Track> collect = imageCaptchaTrack.getTrackList().stream().map(value -> {
//            ImageCaptchaTrack.Track track1 = new ImageCaptchaTrack.Track();
//            track1.setX(value.getX());
//            track1.setY(value.getY());
//            track1.setT(value.getTime());
//            return track1;
//        }).collect(Collectors.toList());
//        track.setTrackList(collect);
        boolean result = imageCaptchaApplication.matching(id, imageCaptchaTrack);
        return result ? R.ok() : R.fail();
    }


    private R sendLoginCode(CodeModel model) {
        //1,先看redis里面是否有，有就不用再次发送
        String redisCode = redisCache.getCacheObject(RedisConst.REDIS_CODE_LOGIN + model.getKey());
        //2,redis没有，则发送并且将其加入到redis中
        if (redisCode == null) {
            redisCode = RandomUtil.randomNumbers(6);
            redisCache.setCacheObject(RedisConst.REDIS_CODE_LOGIN + model.getKey(), redisCode, 5, TimeUnit.MINUTES);
        }
        return R.ok(Code.CODE_SEND_SUCCESS, redisCode);
    }
}
