package com.example.istar.model;

import cn.hutool.core.util.ObjectUtil;
import com.example.istar.model.inter.RequestCheckerInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tian
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class QueryPageModel extends PageModel implements RequestCheckerInterface {
    @ApiModelProperty(value = "关键字")
    private String q;

    @Override
    public boolean isCorrect() {
        return ObjectUtil.isNotEmpty(q);
    }
}
