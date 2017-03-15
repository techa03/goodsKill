<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
    <%@include file="common/head.jsp" %>

    <script type="application/javascript">
        function checkExposer(seckillId) {
            $.ajax({
                url: "/seckill/" + seckillId + "/exposer",
                type: "post",
                success: function (result) {
                    alert($("#phoneNum").val());
                    $.cookie('killPhone', $("#phoneNum").val());
                    console.log("cookie is:" + $.cookie('killPhone'));
                    var data = result.data;
                    console.log(data.exposed);
                    if (data.exposed == true) {
                        $.ajax({
                            url: "/seckill/" + seckillId + "/" + data.md5 + "/execution",
                            type: "post",
                            success: function (result) {
                                var seckillResult = result.data;
                                console.log(seckillResult);
                                alert(seckillResult.statEnum);
                            }
                        });
                    } else {
                        alert("尚未开始");
                    }
                    console.log(data);
                }
            });
        }
    </script>
</head>
<body>
<div class="container">
    <div class="panel panel-default text-center">
        <div class="pannel-heading">${seckillInfo.name}</div>
    </div>
    <div class="panel-body">
        <input type="hidden" name="md5" value="" id="md5"/>
        <input type="text" id="phoneNum" value="">手机号</input>
        <input type="button" value="执行秒杀" onclick="checkExposer(${seckillInfo.seckillId})"/>
    </div>
</div>
</body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
<script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script
        src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
</html>