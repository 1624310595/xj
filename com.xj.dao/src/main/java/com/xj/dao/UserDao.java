package com.xj.dao;

import com.hdh.entrty.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {

    @Select("select * from users where username=#{param1} and password=#{param2}")
    User login(String username, String password);
}
