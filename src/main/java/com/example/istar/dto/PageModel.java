package com.example.istar.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author tian
 */
@NoArgsConstructor()
@AllArgsConstructor()
@ApiModel(value = "分页查询入参")
public class PageModel {
    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @ApiModelProperty(value = "页数", dataType = "Integer")
    private Integer index;
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private Integer count;

    ///TODO 后面要改
    public int getIndex() {
        int safe = 0;
        if (index != null && index >= 0) {
            safe = this.index;
        }
        return safe;
    }

    public int getCount() {
        int safe = 30;
        if (count != null && count >= 0) {
            safe = this.count;
        }
        return safe;
    }

    @ApiModelProperty(hidden = true)
    public int getOffset() {
        return getIndex() * getCount();
    }


}
