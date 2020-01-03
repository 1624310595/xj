<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>数据 - AdminLTE2定制版 | Log in</title>

    <meta
            content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no"
            name="viewport">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/plugins/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/plugins/ionicons/css/ionicons.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/plugins/adminLTE/css/AdminLTE.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/plugins/iCheck/square/blue.css">
</head>

<body class="hold-transition login-page">
<div class="login-box" style="padding-top: 60px">
    <!-- /.login-logo -->
    <div class="login-box-body">
        <p class="login-box-msg">密码修改</p>

        <form method="post">
            <div class="form-group has-feedback">
                <input type="text" name="email" class="form-control" id="email"
                       placeholder="请输入您的邮箱"> <span
                    class="glyphicon glyphicon-envelope form-control-feedback"></span>
            </div>
            <div class="form-group has-feedback" style="display:flex">
                <input type="text" name="code" class="form-control" id="code"
                       placeholder="验证码" style="float: left">
                <input type="button" value="获取验证码" onclick="checkEmail()" id="but"
                       class="btn btn-primary btn-block btn-flat">
            </div>
            <div style="display: none" id="show">
                <div class="form-group has-feedback">
                    <input type="password" name="password" class="form-control" id="password"
                           placeholder="新密码" value="${password}"> <span
                        class="glyphicon glyphicon-lock form-control-feedback"></span>
                </div>
                <div class="form-group has-feedback">
                    <input type="password" name="repassword" class="form-control" id="repassword"
                           placeholder="重复新密码" value="${password}"> <span
                        class="glyphicon glyphicon-lock form-control-feedback"></span>
                </div>
                <span style="font-size: 15px;color: red" id="errMsg">${errMsg}</span>
                <div class="row">
                    <div class="col-xs-4" style="padding-left: 20px">
                        <button type="button" onclick="checkPassword()" class="btn btn-primary btn-block btn-flat"
                                style="margin-left: 232px">确认修改
                        </button>
                    </div>
                    <!-- /.col -->
                </div>
            </div>
        </form>

    </div>
    <!-- /.login-box-body -->
</div>
<!-- /.login-box -->

<!-- jQuery 2.2.3 -->
<!-- Bootstrap 3.3.6 -->
<!-- iCheck -->
<script
        src="${pageContext.request.contextPath}/plugins/jQuery/jquery-2.2.3.min.js"></script>
<script
        src="${pageContext.request.contextPath}/plugins/bootstrap/js/bootstrap.min.js"></script>
<script
        src="${pageContext.request.contextPath}/plugins/iCheck/icheck.min.js"></script>
<script>
    $(function () {
        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%',// optional
        })
        if (sessionStorage.getItem('s') != null && sessionStorage.getItem('s') != 0) {
            $("#but").attr('disabled', true)
            $("#but").attr('font-size', '5px')
            var s = sessionStorage.getItem('s');
            var time = setInterval(function setTime() {
                s = s - 1;
                sessionStorage.setItem('s', s)
                $('#but').val(sessionStorage.getItem('s') + "秒后获取")
                if (sessionStorage.getItem('s') == 0) {
                    clearInterval(time);
                    $("#but").attr('disabled', false)
                    $("#but").val("获取验证码")
                    sessionStorage.removeItem("s")
                }
            }, 1000)
        }
        $("#email").val("")
        $("#code").val("")
    });

    function checkEmail() {
        var reg = /^([a-zA-Z]|[0-9])(\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/;
        var email = $("#email").val();
        if (!reg.test(email)) {
            alert("email格式错误")
            return;
        }
        $("#but").attr('disabled', true)
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/user/checkEmail.do",
            data: "email=" + email,
            success: function (data) {
                if (data.retStatus == "200") {
                    $("#show").show();
                    alert("验证码已经发送请注意查收")
                    $("#but").attr('font-size', '5px')
                    var s = 60;
                    var time = setInterval(function setTime() {
                        sessionStorage.setItem('s', s)
                        $('#but').val(sessionStorage.getItem('s') + "秒后获取")
                        s = s - 1;
                        if (sessionStorage.getItem('s') == 0) {
                            clearInterval(time);
                            $("#but").attr('disabled', false)
                            $("#but").val("获取验证码")
                            sessionStorage.removeItem("s")
                        }
                    }, 1000)

                } else if (data.retStatus == "400") {
                    alert("该邮箱不存在")
                    $("#email").val("");
                } else {
                    alert("请求异常")
                }
            }
        })
    }

    function checkPassword() {
        var flag = true;
        $("input").each(function () {
            if ($(this).val() == "" || $(this).val() == null) {
                $(this).attr('placeholder', '不能为空');
                flag = false;
            }
        })
        if ($("#password").val() != $("#repassword").val() && flag) {
            alert("两次密码不一致")
            return;
        }
        if (flag) {
            $.ajax({
                type: "POST",
                url: "${pageContext.request.contextPath}/user/updatePwd.do",
                data: $("form").serialize(),
                dataType: "json",
                success: function (data) {
                    if (data.retStatus == "200") {
                        sessionStorage.setItem("flag","success")
                        $(location).attr('href', "${pageContext.request.contextPath}/pages/login.jsp?flag=success")
                    } else {
                        alert("密码修改失败")
                    }
                }
            })
        }
        return;
    }
</script>
</body>

</html>