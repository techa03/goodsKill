<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/head.jsp"%>
<html>
<head>
    <title>Title</title>
</head>
<body>

<form role="form" action="/goods/add" enctype="multipart/form-data" method="post" style="width: 30%;">
    <div class="form-group">
        <label for="name">商品名称</label>
        <input type="text" class="form-control" id="name" name="name" placeholder="请输入商品名称">
    </div>
    <div class="form-group">
        <label for="price">价格</label>
        <input type="text" class="form-control" id="price" name="price" placeholder="请输入商品价格">
    </div>
    <div class="form-group">
        <label for="introduce">商品简介</label>
        <input type="text" class="form-control" id="introduce" name="introduce" placeholder="请输入商品介绍">
    </div>
    <div class="form-group">
        <label for="inputFile">上传图片</label>
        <input type="file" class="form-control" name="file" id="inputFile"/>
        <input name="seckillId" value="${seckillId}" type="hidden">
    </div>
    <input type="submit" value="提交"/>
</form>
</body>
<%@include file="common/buttom.jsp" %>
</html>