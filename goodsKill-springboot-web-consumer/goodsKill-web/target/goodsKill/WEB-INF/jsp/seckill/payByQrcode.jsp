<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/tag.jsp" %>
<%@include file="../common/common.jsp" %>
<html>
<head>
    <title>订单支付</title>
</head>
<body>
<form role="form" action="" enctype="multipart/form-data" method="post"
      style="width: 10%;margin: 0 auto;">
    <div class="form-group">
        <label for="Qrcode">请扫码支付：</label>
        <img src="${context}/seckill/Qrcode/${QRfilePath}" id="Qrcode" name="Qrcode"/>
    </div>
</form>
</body>
</html>
