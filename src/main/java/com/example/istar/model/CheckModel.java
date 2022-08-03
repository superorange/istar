package com.example.istar.model;

import cn.hutool.core.util.ObjectUtil;
import com.example.istar.model.inter.RequestCheckerInterface;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author tian
 */
@Data
public class CheckModel implements RequestCheckerInterface {
    final String checkId;
    @Override
    public boolean isCorrect() {
        return ObjectUtil.isNotEmpty(checkId);
    }
}
