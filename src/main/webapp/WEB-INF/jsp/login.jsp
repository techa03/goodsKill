<?xml version="1.0" encoding="UTF-8" ?>
<%@include file="common/head.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
<link
	href="http://cdn.bootcss.com/bootstrap-validator/0.5.3/css/bootstrapValidator.min.css"
	rel="stylesheet">
	<style type="text/css">
</style>
	
</head>
<body>
	<div style="width: 35%; margin: 0 auto;">
		<form id="defaultForm" action="login" role="form"
			style="width: 50%;" method="post">
			<div class="form-group">
				<label for="account">账号：</label> <input type="text"
					class="form-control" name="account" id="account"
					placeholder="6-20位字母数字下划线"></input>
			</div>
			<div class="form-group">
				<label for="password">密码：</label> <input type="password"
					class="form-control" name="password" id="password"></input>
			</div>
			<div class="form-group">
				<button class="btn btn-sm btn-primary btn-block" type="submit">登录</button>
				<a class="btn btn-sm btn-primary btn-block" href="../">注册</a>
			</div>
		</form>
	</div>




</body>



<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script
	src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script
	src="http://cdn.bootcss.com/bootstrap-validator/0.5.3/js/bootstrapValidator.js"></script>
</html>