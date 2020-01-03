package com.xj.service;


import com.hdh.entrty.User;

public interface UserService {
    User login(String username, String password);

    Long checkEmail(String email);

    void updatePwd(String email, String password);
}
