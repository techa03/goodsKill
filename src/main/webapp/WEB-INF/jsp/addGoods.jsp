<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/head.jsp"%>
<html>
<head>
    <title>Title</title>
</head>
<body>

<form role="form" action="" enctype="multipart/form-data" method="post" style="width: 30%;">
    <div class="form-group">
        <label for="name">秒杀商品名称</label>
        <input type="text" class="form-control" id="name" placeholder="请输入商品名称">
    </div>
    <div class="form-group">
        <label for="name">秒杀价格</label>
        <input type="text" class="form-control" id="name" placeholder="请输入商品价格">
    </div>
    <div class="form-group">
        <label for="name">秒杀数量</label>
        <input type="text" class="form-control" id="name" placeholder="请输入商品数量">
    </div>
    <div class="form-group">
        <label for="name">秒杀商品简介</label>
        <input type="text" class="form-control" id="name" placeholder="请输入商品介绍">
    </div>
    <a class="btn btn-info">保存</a>
</form>
</body>
<%@include file="common/buttom.jsp" %>
</html>