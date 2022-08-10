package com.example.istar.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author tian
 */
@Data
public class UserUpdateModel {
    private String nickName;
    private MultipartFile avatar;
    private String signature;
}
