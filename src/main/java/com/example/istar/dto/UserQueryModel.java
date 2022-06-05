package com.example.istar.dto;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class UserQueryModel {
    private String username;
    private String email;
    private String mobile;
    private String uuid;
    private Long id;

    @ApiParam(hidden = true)
    public boolean checkIsNull() {
        return username == null && email == null && mobile == null && uuid == null && id == null;
    }
}
