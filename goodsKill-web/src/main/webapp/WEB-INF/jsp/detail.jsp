<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title></title>
    <%@include file="common/tag.jsp"%>
    <%@include file="common/common.jsp" %>

    <script type="application/javascript">
        function checkExposer(seckillId) {
            $.ajax({
                url: "${context}/seckill/" + seckillId + "/exposer",
                type: "post",
                success: function (result) {
                    $.cookie('killPhone', $("#phoneNum").val());
                    console.log("cookie is:" + $.cookie('killPhone'));
                    var data = result.data;
                    if (data.exposed == true) {
                        $.ajax({
                            url: "${context}/seckill/" + seckillId + "/" + data.md5 + "/execution",
                            type: "post",
                            success: function (result) {
                                var seckillResult = result.data;
                                console.log(seckillResult);
                                if(seckillResult.statEnum=="SUCCESS"){
                                    window.open("${context}/seckill/pay/Qrcode/"+seckillResult.qrfilepath);
                                }else {
                                    alert(seckillResult.statEnum);
                                }
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
    <form class="form-inline">
        <div class="form-group">
            <label for="phoneNum">手机号</label>
            <input type="text" class="form-control" id="phoneNum" name="手机" placeholder="请输入11为手机号" maxlength="11">
        </div>
        <button type="button" class="btn btn-default" onclick="checkExposer(${seckillInfo.seckillId})">执行秒杀</button>
    </form>
    <div class="panel-body">
        <input type="hidden" name="md5" value="" id="md5"/>
    </div>
</div>
</body>

</html>