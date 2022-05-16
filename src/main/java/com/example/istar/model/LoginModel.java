package com.example.istar.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginModel {
    private final String username;
    private final String password;
}
