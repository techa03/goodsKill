<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../common/head.jsp"%>
<html>
<head>
    <title>增加秒杀商品详情</title>
    <script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
    <script type="application/javascript">
       $(function(){
           $.ajax({
               url: "/goods/list",
               success: function(data){
                   var str="";
                   for(var o in data){
                       str+="<option value='"+data[o].goodsId+"'>"+data[o].name+"</option>";
                   }
                   $("#seckillId").html(str);
               }
           });

       });
    </script>
</head>
<body>

<form role="form" action="/seckill/addSeckill" enctype="multipart/form-data" method="post" style="width: 10%;margin: 0 auto;">
    <div class="form-group">
        <div class="dropdown">
            <label for="goodsId">秒杀商品：</label></br>
            <select class="form-control" name="goodsId" id="goodsId">

            </select>
        </div>
    </div>
    <div class="form-group">
        <label for="name">秒杀价格：</label>
        <input type="text" class="form-control" id="name" name="name" placeholder="请输入秒杀活动名称">
    </div>
    <%--<div class="form-group">--%>
        <%--<label for="introduce">秒杀活动商品介绍：</label>--%>
        <%--<input type="text" class="form-control" id="introduce" name="introduce" placeholder="请输入商品介绍">--%>
    <%--</div>--%>

    <input type="submit" class="form-control" value="新增秒杀"/>
</form>
</body>
<%@include file="../common/buttom.jsp" %>
</html>