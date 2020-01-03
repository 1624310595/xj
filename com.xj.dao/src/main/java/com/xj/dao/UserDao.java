package com.xj.dao;

import com.hdh.entrty.User;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {

    @Select("select * from users where username=#{param1} and password=#{param2}")
    User login(String username, String password);


    @Select("select count(*) from users where email=#{email}")
    Long checkEmail(String email);

    @Update("update users set password=#{param2} where email=#{param1}")
    void updatePwd(String email, String password);
}
