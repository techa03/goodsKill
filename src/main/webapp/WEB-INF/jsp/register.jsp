<?xml version="1.0" encoding="UTF-8" ?>
<%@include file="common/head.jsp" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>
<body>
	<div style="width:35%;margin:0 auto;">
		<form action="register" role="form" style="width:100%;" method="post">
		<div class="form-group">
			<label for="account" >创建账号：</label>
			<input type="text" class="form-control" name="account" id="account" placeholder="6-20位字母数字以及下划线"></input>
		</div>
		<div class="form-group">
			<label for="password" >创建密码：</label>
			<input type="text" class="form-control" name="password" id="password" placeholder="6-20数字字母混合密码"></input>
		</div>
		<div class="form-group">
			<label for="password2" >再次输入：</label>
			<input type="text" class="form-control" id="password2" placeholder="请保持输入密码一致"></input>
		</div>
		<div class="form-group">
			<button type="submit" class="btn btn-default">注册</button>
		</div>
	</form>
	</div>
	
	
	
	
</body>

<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>

	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script
		src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
</html>