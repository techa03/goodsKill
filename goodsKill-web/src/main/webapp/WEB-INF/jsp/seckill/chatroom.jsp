<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/tag.jsp" %>
<%@include file="../common/common.jsp" %>
<html>
<script type="application/javascript">
    function getMessageList() {
        $.ajax({
            url: "${context}/chatroom/messageList/get",
            success: function (data) {
                $("#messageList").html(data);
            }
        });
    }

    // 50ms刷新聊天记录页面
    $(setInterval(getMessageList, 50));
</script>
<head>
    <title>用户聊天室</title>
</head>
<body>
<form role="form" action="${context}/chatroom/send" enctype="multipart/form-data" method="post"
      style="width: 50%;margin: 0 auto;">

    <div class="form-group">
        <textarea class="form-control" rows="25" readonly name="messageList" id="messageList"></textarea>
        <div>&nbsp;</div>
        <textarea class="form-control" rows="5" name="message"></textarea>
    </div>
    <input type="submit" class="btn btn-info" value="发送"/>
</form>
</body>
</html>
