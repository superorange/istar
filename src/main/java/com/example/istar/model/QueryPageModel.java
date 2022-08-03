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
public class QueryPageModel extends PageModel {
    @ApiModelProperty(value = "查询关键字，like")
    private String q;
    @ApiModelProperty("查询谁的，uuid/等")
    private String key;
}
