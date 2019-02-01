<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/tag.jsp" %>
<%@include file="../common/common.jsp" %>
<html>
<head>
    <title>用户聊天室</title>
</head>
<body>
<form role="form" action="${context}/chatroom/send" enctype="multipart/form-data" method="post"
      style="width: 50%;margin: 0 auto;">
    <div class="form-group">
        <textarea class="form-control" rows="5" name="message"></textarea>
    </div>
    <input type="submit" class="btn btn-info" value="发送"/>
</form>
</body>
</html>
