package com.xj.service.impl;

import com.hdh.entrty.User;
import com.xj.dao.UserDao;
import com.xj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User login(String username, String password) {
        return userDao.login(username, password);
    }

    @Override
    public Long checkEmail(String email) {

        return userDao.checkEmail(email);
    }

    @Override
    public void updatePwd(String email, String password) {
         userDao.updatePwd(email, password);
    }
}
