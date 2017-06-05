<?xml version="1.0" encoding="UTF-8" ?>
<%@include file="common/common.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Insert title here</title>
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <style type="text/css">
    </style>
    <script type="text/javascript">
        $(document)
            .ready(
                function () {
                    /**
                     * 下面是进行插件初始化
                     * 你只需传入相应的键值对
                     * */
                    $('#defaultForm')
                        .bootstrapValidator(
                            {
                                message: 'This value is not valid',
                                feedbackIcons: {
                                    /*输入框不同状态，显示图片的样式*/
                                    valid: 'glyphicon glyphicon-ok',
                                    invalid: 'glyphicon glyphicon-remove',
                                    validating: 'glyphicon glyphicon-refresh'
                                },
                                fields: {
                                    /*验证*/
                                    account: {
                                        /*键名username和input name值对应*/
                                        message: 'The username is not valid',
                                        validators: {
                                            notEmpty: {
                                                /*非空提示*/
                                                message: '账号不能为空'
                                            },
                                            regexp: {
                                                /* 只需加此键值对，包含正则表达式，和提示 */
                                                regexp: /^[a-zA-Z0-9_\.]+$/,
                                                message: '只能是数字和字母_.'
                                            },
                                            stringLength: {
                                                /*长度提示*/
                                                min: 6,
                                                max: 20,
                                                message: '用户名长度必须在6到20之间'
                                            }
                                            /*最后一个没有逗号*/
                                        }
                                    },
                                    password: {
                                        message: '密码无效',
                                        validators: {
                                            notEmpty: {
                                                message: '密码不能为空'
                                            },
                                            regexp: {
                                                /* 只需加此键值对，包含正则表达式，和提示 */
                                                regexp: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/,
                                                message: '只能是数字和字母混合密码'
                                            },
                                            identical: {//相同
                                                field: 'password', //需要进行比较的input name值
                                                message: '两次密码不一致'
                                            },
                                            different: {//不能和用户名相同
                                                field: 'account',//需要进行比较的input name值
                                                message: '不能和账号名相同'
                                            },
                                            stringLength: {
                                                min: 6,
                                                max: 30,
                                                message: '账号长度必须在6到20之间'
                                            }
                                        }
                                    },
                                    password2: {
                                        message: '密码无效',
                                        validators: {
                                            notEmpty: {
                                                message: '密码不能为空'
                                            },
                                            regexp: {
                                                /* 只需加此键值对，包含正则表达式，和提示 */
                                                regexp: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/,
                                                message: '只能是数字和字母混合密码'
                                            },
                                            identical: {//相同
                                                field: 'password', //需要进行比较的input name值
                                                message: '两次密码不一致'
                                            },
                                            different: {//不能和用户名相同
                                                field: 'account',//需要进行比较的input name值
                                                message: '不能和账号名相同'
                                            },
                                            stringLength: {
                                                min: 6,
                                                max: 30,
                                                message: '账号长度必须在6到20之间'
                                            }
                                        }
                                    },
                                    email: {
                                        validators: {
                                            notEmpty: {
                                                message: 'The email address is required and can\'t be empty'
                                            },
                                            emailAddress: {
                                                message: 'The input is not a valid email address'
                                            }
                                        }
                                    },
                                    phoneNum: {
                                        message: '手机号无效',
                                        validators: {
                                            notEmpty: {
                                                message: '不能为空'
                                            },
                                            regexp: {
                                                /* 只需加此键值对，包含正则表达式，和提示 */
                                                regexp: /^1[3|4|5|7|8][0-9]\d{4,8}$/,
                                                message: '手机号输入不正确'
                                            },
                                            stringLength: {
                                                min: 11,
                                                max: 11,
                                                message: '号码长度必须为11位'
                                            }
                                        }
                                    }
                                }

                            })

                        .on(
                            'success.form.bv',
                            function (e) {//点击提交之后
                                // Prevent form submission
                                e.preventDefault();

                                // Get the form instance
                                var $form = $(e.target);

                                // Get the BootstrapValidator instance
                                var bv = $form
                                    .data('bootstrapValidator');

                                // Use Ajax to submit form data 提交至form标签中的action，result自定义
                                $.post($form.attr('action'),
                                    $form.serialize(),
                                    function (result) {
                                        //do something...
                                        console.log(result);
                                        window.location.href = "./list";
                                    });
                            });
                });
        function getPhoneCode(){
            var reg=/^1[3|4|5|7|8][0-9]\d{4,8}$/;
            var phoneNum=$("#phoneNum").val().trim();
            if(phoneNum.match(reg)==null){
                alert("请输入正确的手机号码！");
            }else{
                $.ajax({
                    type:"post",
                    url:"user/"+phoneNum+"/phoneCode",
                    success:function (data) {
                        
                    }

                });
            }
        }
    </script>
</head>
<body>
<div style="width: 35%; margin: 0 auto;">
    <form id="defaultForm" action="register" role="form"
          style="width: 50%;" method="post">
        <div class="form-group">
            <label for="account">账号：</label>
            <input type="text"
                   class="form-control" name="account" id="account"
                   placeholder="6-20位字母数字下划线"/>
        </div>
        <div class="form-group">
            <label for="phoneNum">手机号：</label>
            <input type="phoneNum" id="phoneNum"
                   class="form-control" name="phoneNum"
                   placeholder="请输入手机号" maxlength="11"/>
            <span><button type="button" class="btn btn-info" onclick="getPhoneCode()">获取验证码</button></span>
        </div>
        <div class="form-group">
            <label for="password">密码：</label>
            <input type="password"
                   class="form-control" name="password" id="password"
                   placeholder="6-20数字字母混合密码"/>
        </div>
        <div class="form-group">
            <label for="password2">再次输入密码：</label>
            <input type="password"
                   class="form-control" name="password2"
                   placeholder="请再次输入密码"/>
        </div>
        <!-- 		<div class="form-group"> -->
        <!-- 			<label for="password2" style="display:block;">手机验证码：</label> -->
        <!-- 			<input type="text" class="form-control" name="phoneCode" id="phoneCode" placeholder="输入手机收到的验证码" style="display:inline;"></input> -->
        <!-- 			<button type="button" class="btn btn-info">获取短信验证码</button> -->
        <!-- 			<p></p> -->

        <!-- 		</div> -->
        <div class="form-group">
            <button class="btn btn-sm btn-primary btn-block" type="submit">确认注册</button>
            <a class="btn btn-sm btn-primary btn-block" href="../">返回首页</a>
        </div>
    </form>
</div>
</body>
</html>