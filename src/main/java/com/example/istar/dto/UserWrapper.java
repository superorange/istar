package com.example.istar.dto;

import com.example.istar.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWrapper {
    private final String token;
    private final User user;

}
