package com.xj.web.controller;

import com.hdh.entrty.UpdatePwd;
import com.hdh.entrty.User;
import com.xj.service.UserService;
import com.xj.utils.ResultSet;
import com.xj.utils.SendEmail;
import com.xj.utils.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 功能描述:用户登录
     *
     * @author: hdh
     * @date: 2019/12/26 13:44
     */
    @PostMapping("login.do")
    public ModelAndView login(HttpSession session, String username, String password, HttpServletRequest request, HttpServletResponse response,
                              @RequestParam(value = "rememberMe", defaultValue = "") String[] rememberMe) {
        ModelAndView mav = new ModelAndView();
        User user = userService.login(username, password);

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
    @GetMapping("logout.do")
    public ModelAndView logout(HttpSession session, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        session.removeAttribute(IP);
        session.removeAttribute(USER_NAME);

        mav.setView(new RedirectView(request.getContextPath() + "/pages/login.jsp"));
        return mav;
    }

    /**
     * 功能描述：查看cookie是否自动登陆
     * cookie存放时间 7天
     *
     * @author:xj
     * @date: 2019/12/26 17:50
     */
    @GetMapping("autoLogin.do")
    public ModelAndView autoLogin(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 1) {

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("rememberAccount")) {
                    String account = cookie.getValue();
                    String[] user = account.split("/");

                    mav.addObject(USER_NAME, user[0]);
                    mav.addObject(Password, user[1]);
                }
            }
        }
        mav.setViewName("login");
        return mav;
    }

    /**
     * 功能描述：  检测邮箱是否存在
     * 存在：发送携带token的邮件
     * 不存在：返回错误信息
     *
     * @author:xj
     * @date: 2020/1/2 16:36
     */
    @RequestMapping("checkEmail.do")
    @ResponseBody
    public ResultSet checkEmail(@RequestBody @RequestParam("email") String email, HttpSession session) {
        ResultSet rs = new ResultSet();
        Long checkEmail = userService.checkEmail(email);
        if (checkEmail == 0) {
            rs.setRetStatus(FAIL_CODE);
            return rs;
        }
        try {
            String uuid = UUID.getUUiID();
            SendEmail.sendEmail(email, uuid);
            session.setAttribute("token", uuid);
            session.setAttribute("email", email);
        } catch (Exception e) {
            rs.setRetStatus(FAIL_CODE);
            return rs;
        }
        rs.setRetStatus(SUCCESS_CODE);
        return rs;
    }

    /**
     * 功能描述：
     *
     * @author:xj 修改密码
     * @date: 2020/1/3 8:50
     */

    @RequestMapping("updatePwd.do")
    @ResponseBody
    public ResultSet updatePwd(HttpSession session, UpdatePwd updatePwd) {
        ResultSet rs = new ResultSet();
        try {
            String uuid = (String) session.getAttribute("token");
            String email = (String) session.getAttribute("email");
            if (!(uuid.equals(updatePwd.getCode()) && email.equals(updatePwd.getEmail()))) {
                rs.setRetStatus(FAIL_CODE);
                return rs;
            }
            userService.updatePwd(email, updatePwd.getPassword());

            rs.setRetStatus(SUCCESS_CODE);
            return rs;
        } catch (Exception e) {
            rs.setRetStatus(FAIL_CODE);
            return rs;
        }
    }
}
