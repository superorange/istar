package com.example.istar.model;

import lombok.Data;

/**
 * @author tian
 */
@Data
public class RegisterModel {
    String userName;
    String password;

    public boolean isValid() {
        return userName != null && userName.length() > 0 && password != null && password.length() > 0;
    }
}
