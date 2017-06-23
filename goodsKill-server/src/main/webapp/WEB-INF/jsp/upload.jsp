<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/common.jsp"%>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form role="form" action="${seckillId}/create" enctype="multipart/form-data" method="post">
        <div class="form-group" style="width: 15%;margin: 0 auto;">
            <label for="inputFile">上传图片</label>
            <input type="file" class="form-control" name="file" id="inputFile"/>
            <input name="seckillId" value="${seckillId}" type="hidden">
            <input type="submit" value="提交"/>
        </div>
    </form>
</body>
</html>
