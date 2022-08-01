package com.example.istar.model;

import com.example.istar.model.PageModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tian
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TopicQueryModel extends QueryPageModel {
    @ApiModelProperty("查询谁的，owner为self查询自己")
    private String owner;
}
