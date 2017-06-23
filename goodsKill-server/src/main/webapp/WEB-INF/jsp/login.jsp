<?xml version="1.0" encoding="UTF-8" ?>
<%@include file="common/common.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>

</head>
<body>
	<div style="width: 35%; margin: 0 auto;">
		<form id="defaultForm" action="login/session" role="form"
			style="width: 50%;" method="post">
			<div class="form-group">
				<label for="account">账号：</label> <input type="text"
					class="form-control" name="account" id="account"
					placeholder="6-20位字母数字下划线"></input>
			</div>
			<div class="form-group">
				<label for="password">密码：</label> <input type="password"
					class="form-control" name="password" id="password" placeholder="请输入密码"/>
			</div>
			<div class="form-group">
				<button class="btn btn-sm btn-primary btn-block" type="submit">登录</button>
				<a class="btn btn-sm btn-primary btn-block" href="register">注册</a>
			</div>
		</form>
	</div>
</body>



</html>