package com.example.istar.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.example.istar.common.Code;
import com.example.istar.common.RedisConst;
import com.example.istar.model.CodeModel;
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
    private RedisUtil redisUtil;
    @Resource
    private ImageCaptchaApplication imageCaptchaApplication;

    @ApiOperation(value = "发送验证码", notes = "用户发送验证码")
    @PostMapping("/send")
    public Res sendCode(@RequestBody CodeModel model) throws Exp {
        model.check();
        //1,先看redis里面是否有，有就不用再次发送
        String redisCode = redisUtil.getCacheObject(RedisConst.AUTH_CODE_BY_KEY + model.getData());
        if (redisCode != null) {
            return Res.ok(Code.CODE_SEND_SUCCESS, redisCode);
        }
        String key = RedisConst.AUTH_CID_BY_KEY + model.getData();
        String cacheObject = redisUtil.getCacheObject(key);
        if (ObjectUtil.isNull(cacheObject)) {
            return Res.fail(ErrorMsg.OPERATION_FORBIDDEN);
        }
        redisUtil.deleteObject(key);
        return sendLoginCode(model);
    }

    @ApiOperation(value = "生成滑块验证", notes = "用户生成滑块验证")
    @PostMapping("/gen")
    public Res<CaptchaResponse<ImageCaptchaVO>> genCode() {
        CaptchaResponse<ImageCaptchaVO> res1 = imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
//        CaptchaResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(CaptchaImageType.WEBP);
        return Res.ok(res1);
    }

    @ApiOperation(value = "校验滑块轨迹", notes = "滑块轨迹校验")
    @PostMapping("/check")
    @ResponseBody
    public Res checkCaptcha(@RequestParam("id") String id,
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
        return result ? Res.ok() : Res.fail(6999, "验证失败");
    }


    private Res<String> sendLoginCode(CodeModel model) {
        //1,先看redis里面是否有，有就不用再次发送
        String redisCode = redisUtil.getCacheObject(RedisConst.AUTH_CODE_BY_KEY + model.getData());
        //2,redis没有，则发送并且将其加入到redis中
        if (redisCode == null) {
            redisCode = RandomUtil.randomNumbers(6);
            redisUtil.setCacheObject(RedisConst.AUTH_CODE_BY_KEY + model.getData(), redisCode, 5, TimeUnit.MINUTES);
        }
        return Res.ok(Code.CODE_SEND_SUCCESS, redisCode);
    }
}
