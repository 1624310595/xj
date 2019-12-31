package com.xj.web.controller;

import com.hdh.entrty.User;
import com.xj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.xj.utils.InitConstants.*;

/**
 * 功能描述:user控制层
 *
 * @author: hdh
 * @date: 2019/12/26 13:38
 */
@Controller
public class UserController {
    @Autowired
    private UserService service;

    /**
     * 功能描述:用户登录
     *
     * @author: hdh
     * @date: 2019/12/26 13:44
     */
    @PostMapping("/user/login.do")
    public ModelAndView login(HttpSession session, String username, String password, HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "rememberMe", defaultValue = "") String[] rememberMe) {
        ModelAndView mav = new ModelAndView();
        User user = service.login(username, password);

        //如果不是是同一个ip登陆 视为重复登陆
        String ipAddress = request.getRemoteAddr();
        if (ipAddress.equals("0:0:0:0:0:0:0:1")) {
            ipAddress = "127.0.0.1";
        }
        if (user == null) {
            //mav.addObject("errMsg","用户名或密码错误");
            mav.addObject(ERR_MSG, "用户名或密码错误");
            mav.setViewName("login");
            return mav;
        } else {
            String sessionIp = (String) session.getAttribute(IP);
            if (sessionIp != null && ipAddress != sessionIp) {
                mav.addObject(ERR_MSG, "您已登陆过请不要重复登陆");
                mav.setViewName("login");
                return mav;
            }
        }
        if (rememberMe.length > 0) {
            Cookie cookie = new Cookie("rememberAccount", user.getUsername() + "/" + user.getPassword());
            cookie.setMaxAge(Integer.valueOf(rememberMe[0]) * 24 * 60 * 60);
            response.addCookie(cookie);
        }

        session.setAttribute(IP, ipAddress);
        session.setAttribute(USER_NAME, user.getUsername());
        mav.setView(new RedirectView(request.getContextPath() + "/pages/main.jsp"));
        return mav;
    }

    /**
     * 功能描述：用户注销
     *
     * @author:xj
     * @date: 2019/12/26 15:59
     */
    @GetMapping("/user/logout.do")
    public ModelAndView logout(HttpSession session,HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        session.removeAttribute(IP);
        session.removeAttribute(USER_NAME);

        mav.setView(new RedirectView(request.getContextPath() + "/pages/login.jsp"));
        return mav;
    }

    /**
     * 功能描述：查看cookie是否自动登陆
     *           cookie存放时间 7天
     *
     * @author:xj
     * @date: 2019/12/26 17:50
     */
    @GetMapping("/user/autoLogin.do")
    public ModelAndView autoLogin(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        Cookie[] cookies = request.getCookies();
        if (cookies!=null && cookies.length > 1) {

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("rememberAccount")){
                    String account=cookie.getValue();
                    String[] user = account.split("/");

                    mav.addObject(USER_NAME, user[0]);
                    mav.addObject(Password, user[1]);
                }
            }

        }
        mav.setViewName("login");
        return mav;
    }
}
