package com.example.istar.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author tian
 */
@NoArgsConstructor()
@AllArgsConstructor()
@Setter
@Getter
@ApiModel(value = "分页查询入参")
public class PageModel {
    @ApiModelProperty(value = "页数", dataType = "Integer")
    private Integer index;
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private Integer count;
    @ApiModelProperty(value = "排序方式(asc或者desc,默认降序desc)")
    private String order;


    /**
     * /TODO 后面要改
     */
    @ApiModelProperty(hidden = true)
    public int getCurrentIndex() {
        int safe = 0;
        if (index != null && index >= 0) {
            safe = this.index;
        }
        return safe;
    }

    @ApiModelProperty(hidden = true)
    public int getCurrentCount() {
        int safe = 30;
        if (count != null && count >= 0) {
            safe = this.count;
        }
        return safe;
    }

    @ApiModelProperty(hidden = true)
    public int getOffset() {
        return getCurrentIndex() * getCurrentCount();
    }

    @ApiModelProperty(hidden = true)
    public boolean isAsc() {
        return "asc".equalsIgnoreCase(order);
    }


}
