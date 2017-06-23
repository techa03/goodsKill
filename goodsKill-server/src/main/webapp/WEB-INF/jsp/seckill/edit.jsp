<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/tag.jsp"%>
<%@include file="../common/common.jsp" %>
<html>
<head>
    <title>增加秒杀商品详情</title>
    <script type="application/javascript">
        $(function () {
            $.ajax({
                url: "${context}/goods/list",
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
                url: "${context}/goods/" + goodsId,
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

<form role="form" action="${context}/seckill/${seckillInfo.seckillId}/update" enctype="multipart/form-data" method="post"
      style="width: 10%;margin: 0 auto;">
    <div class="form-group">
        <label for="name">秒杀商品名称：</label>
        <input type="text" class="form-control" id="goodsName" name="goodsName" value="${seckillInfo.goodsName}" readonly="readonly">
    </div>
    <div class="form-group">
        <label for="price">活动名称：</label>
        <input type="text" class="form-control" id="name" name="name" value="${seckillInfo.name}">
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
    <input type="submit" class="form-control" value="确认修改"/>
</form>
</body>
</html>