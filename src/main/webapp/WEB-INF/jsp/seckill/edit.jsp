<%@ taglib prefix="c" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/head.jsp" %>
<html>
<head>
    <title>增加秒杀商品详情</title>
    <script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
    <script type="application/javascript">
        $(function () {
            $.ajax({
                url: "/goods/list",
                success: function (data) {
                    var str = "";
                    for (var o in data) {
                        str += "<option value='" + data[o].goodsId + "'>" + data[o].name + "</option>";
                    }
                    $("#goodsId").html(str);
                }
            });

        });
        function getPrice() {
            var goodsId = $("#goodsId").val();
            $.ajax({
                url: "/goods/getGoodsById/" + goodsId,
                success: function (data) {
                    $("#realGoodsPrice").html("实际价格:" + data.price);
                }
            });
        };
        $(function () {
            $('#datetimepicker1,#datetimepicker2').datetimepicker({
                format: 'YYYY-MM-DD hh:mm',
                locale: moment.locale('zh-cn')
            });
        });
    </script>
</head>
<body>

<form role="form" action="/seckill/${seckillInfo.seckillId}/update" enctype="multipart/form-data" method="post"
      style="width: 10%;margin: 0 auto;">
    <div class="form-group">
        <label for="name">秒杀商品名称：</label>
        <input type="text" class="form-control" id="name" name="name" value="${seckillInfo.goodsName}" readonly="readonly">
    </div>
    <div class="form-group">
        <label for="price">秒杀价格：</label>
        <input type="text" class="form-control" id="price" name="price" value="${seckillInfo.price}">
        <div id="realGoodsPrice"></div>
    </div>
    <div class="form-group">
        <label for="number">秒杀数量：</label>
        <input type="text" class="form-control" id="number" name="number" value="${seckillInfo.number}">
    </div>
    <div class="form-group">
        <label>选择秒杀开始日期：</label>
        <!--指定 date标记-->
        <div class='input-group date' id='datetimepicker1'>
            <input type='text' class="form-control" name="startTime"
                   value="<fmt:formatDate value="${seckillInfo.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
            <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
        </div>
    </div>
    <div class="form-group">
        <label>选择秒杀结束日期：</label>
        <!--指定 date标记-->
        <div class='input-group date' id='datetimepicker2'>
            <input type='text' class="form-control" name="endTime"
                   value="<fmt:formatDate value="${seckillInfo.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
            <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
        </div>
    </div>
    <%--<div class="form-group">--%>
    <%--<label for="introduce">秒杀活动商品介绍：</label>--%>
    <%--<input type="text" class="form-control" id="introduce" name="introduce" placeholder="请输入商品介绍">--%>
    <%--</div>--%>

    <input type="submit" class="form-control" value="确认修改"/>
</form>
</body>
<%@include file="../common/buttom.jsp" %>
</html>