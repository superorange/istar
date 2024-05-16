package com.example.istar.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.example.istar.common.Const;
import com.example.istar.common.RedisConst;
import com.example.istar.model.CodeModel;
import com.example.istar.utils.*;
import com.example.istar.utils.response.ErrorException;
import com.example.istar.utils.response.ErrorMsg;
import com.example.istar.utils.response.ResEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.Null;
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
    public ResEntity<String> sendCode(@RequestBody CodeModel model) throws ErrorException {
        model.check();
        //1,先看redis里面是否有，有就不用再次发送
        String redisCode = redisUtil.getCacheObject(RedisConst.AUTH_CODE_BY_KEY + model.getKey());
        if (redisCode != null) {
            return ResEntity.ok(Const.CODE_SEND_SUCCESS, redisCode);
        }
        String key = RedisConst.AUTH_CID_BY_KEY + model.getKey();
        String cacheObject = redisUtil.getCacheObject(key);
        if (ObjectUtil.isNull(cacheObject)) {
            return ResEntity.fail(ErrorMsg.UNAUTHORIZED);
        }
        redisUtil.deleteObject(key);
        return sendLoginCode(model);
    }

    @ApiOperation(value = "生成滑块验证", notes = "用户生成滑块验证")
    @PostMapping("/gen")
    public ResEntity<CaptchaResponse<ImageCaptchaVO>> genCode() {
        CaptchaResponse<ImageCaptchaVO> res1 = imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
//        CaptchaResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(CaptchaImageType.WEBP);
        return ResEntity.ok(res1);
    }

    @ApiOperation(value = "校验滑块轨迹", notes = "滑块轨迹校验")
    @PostMapping("/check")
    @ResponseBody
    public ResEntity<Null> checkCaptcha(@RequestParam("id") String id,
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
        return result ? ResEntity.ok() : ResEntity.fail(6999, "验证失败");
    }


    private ResEntity<String> sendLoginCode(CodeModel model) {
        //1,先看redis里面是否有，有就不用再次发送
        String redisCode = redisUtil.getCacheObject(RedisConst.AUTH_CODE_BY_KEY + model.getKey());
        //2,redis没有，则发送并且将其加入到redis中
        if (redisCode == null) {
            redisCode = RandomUtil.randomNumbers(6);
            redisUtil.setCacheObject(RedisConst.AUTH_CODE_BY_KEY + model.getKey(), redisCode, 5, TimeUnit.MINUTES);
        }
        return ResEntity.ok(Const.CODE_SEND_SUCCESS, redisCode);
    }
}
