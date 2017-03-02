<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@include file="common/tag.jsp" %>
<%@include file="common/head.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>秒杀列表页</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<script type="application/javascript">
//		function checkExposer(seckillId) {
//			$.ajax({
//			    url:"/seckill/"+seckillId+"/exposer",
//				type:"post",
//				success:function (result) {
//                    var data = result.data;
//                    console.log(data.exposed);
//			        if(data.exposed==true){
//			            window.open("/seckill/"+seckillId+"/"+data.md5+"/execution");
//					}else{
//                        alert("尚未开始");
//					}
//					console.log(data);
//                }
//			});
//        }
	</script>
</head>
<body>
	<div class="container">
		<div class="panel panel-default">
			<div class="panel-heading text-center">
				<h2>秒杀列表</h2>
			</div>
			<div class="panel-body">
				<table class="table table-hover">
                    <a class="btn btn-info" href="/seckill/toAddSeckill" target="_blank">增加秒杀商品</a>
					<a>&nbsp;</a>
					<a class="btn btn-info" href="/seckill/toAddGoods" target="_blank">增加商品种类</a>
					<thead>
						<tr>
							<th>名称</th>
							<th>图片</th>
							<th>库存</th>
							<th>开始时间</th>
							<th>结束时间</th>
							<th>创建时间</th>
							<th>详情页</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="sk" items="${list}">
							<tr>
								<td>${sk.name}</td>
								<td><img alt="图片" src="http://localhost:8080/seckill/img/seckill/${sk.seckillId}" style="width: 80px;height: 80px;"></td>
								<td>${sk.number}</td>
								<td>
									<fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
								</td>
								<td>
									<fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
								</td>
								<td>
									<fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
								</td>
								<td>
									<a class="btn btn-info" href="/seckill/toUploadPhoto/${sk.seckillId}" target="_blank">上传图片</a>
									<a class="btn btn-info"  target="_blank" href="/seckill/${sk.seckillId}/detail" onclick="checkExposer(${sk.seckillId})">链接</a>
									<a class="btn btn-info" href="/seckill/${sk.seckillId}/delete"
									   target="_blank">删除</a>
									<a class="btn btn-info" href="/seckill/${sk.seckillId}/update"
									   target="_blank">修改</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>

			</div>
		</div>
	</div>
</body>
	
<%@include file="common/buttom.jsp"%>
</html>