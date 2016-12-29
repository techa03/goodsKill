<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/head.jsp"%>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form role="form" action="uploadPhoto" enctype="multipart/form-data" method="post">
        <div class="form-group">
            <label for="inputFile">上传图片aa</label>
            <input type="file" class="form-control" name="file" id="inputFile"/>
            <input type="submit" value="提交"/>
        </div>
    </form>
</body>
<%@include file="common/buttom.jsp" %>
</html>
