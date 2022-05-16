package com.example.istar.dto;

import lombok.Data;
import org.springframework.util.ObjectUtils;

/**
 * @author tian
 */
@Data
public class RegisterDTO {
    String username;
    String password;
    String email;

    public boolean checkOk() {
        return !ObjectUtils.isEmpty(username) && username.length() >= 6 && username.length() <= 18 && !ObjectUtils.isEmpty(password) && password.length() >= 6 && password.length() <= 30;
    }
}
