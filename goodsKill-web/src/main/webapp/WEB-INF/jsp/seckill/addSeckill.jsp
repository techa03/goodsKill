<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/tag.jsp" %>
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

<form role="form" action="${context}/seckill/create" enctype="multipart/form-data" method="post"
      style="width: 10%;margin: 0 auto;">
    <div class="form-group">
        <div class="dropdown">
            <label for="goodsId">秒杀商品：</label></br>
            <select class="form-control" name="goodsId" id="goodsId" onchange="getPrice()">
            </select>
        </div>
    </div>
    <div class="form-group">
        <label for="name">秒杀活动名称：</label>
        <input type="text" class="form-control" id="name" name="name" placeholder="请输入秒杀活动名称">
    </div>
    <div class="form-group">
        <label for="price">秒杀价格：</label>
        <input type="text" class="form-control" id="price" name="price" placeholder="请输入秒杀价格">
        <div id="realGoodsPrice"></div>
    </div>
    <div class="form-group">
        <label for="number">秒杀数量：</label>
        <input type="text" class="form-control" id="number" name="number" placeholder="请输入秒杀数量">
    </div>
    <div class="form-group">
        <label>选择秒杀开始日期：</label>
        <!--指定 date标记-->
        <div class='input-group date' id='datetimepicker1'>
            <input type='text' class="form-control" name="startTime"/>
            <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
        </div>
    </div>
    <div class="form-group">
        <label>选择秒杀开始日期：</label>
        <!--指定 date标记-->
        <div class='input-group date' id='datetimepicker2'>
            <input type='text' class="form-control" name="endTime"/>
            <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
        </div>
    </div>
    <input type="submit" class="btn btn-info" value="新增秒杀"/>
</form>
</body>
</html>