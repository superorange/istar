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
@Setter
@ApiModel(value = "分页查询入参")
public class PageModel {
    @ApiModelProperty(value = "页数", dataType = "Integer")
    private String index;
    @ApiModelProperty(value = "数量", dataType = "Integer")
    private String count;
    @ApiModelProperty(value = "排序方式(asc或者desc,默认降序desc)")
    private String order;


    /**
     * /TODO 后面要改
     */
    @ApiModelProperty(hidden = true)
    public int getCurrentIndex() {
        int safe = 0;
        try {
            int i = Integer.parseInt(index);
            if (i >= 0) {
                safe = i;
            }
        } catch (NumberFormatException ignored) {
        }
        return safe;
    }

    @ApiModelProperty(hidden = true)
    public int getCurrentCount() {
        int safe = 30;
        try {
            int i = Integer.parseInt(count);
            if (i >= 0) {
                safe = i;
            }
        } catch (NumberFormatException ignored) {
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
