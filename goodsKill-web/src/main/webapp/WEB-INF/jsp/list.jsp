<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@include file="common/tag.jsp" %>
<%@include file="common/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>秒杀列表页</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script type="application/javascript">
        function changePageNum() {
            var pageNum=$("#pageNum").val();
            window.location.href="${context}/seckill/list?limit="+pageNum;
        }
    </script>
</head>
<body>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading text-center">
            <h2 class="text-primary">秒杀列表</h2>
        </div>
        <div style="margin-top: 15px;margin-left: 15px">
            <a class="btn btn-info" href="${context}/seckill/new" target="_blank">增加秒杀商品</a>
            <a>&nbsp;</a>
            <a class="btn btn-info" href="${context}/goods/new" target="_blank">增加商品种类</a>
        </div>
        <div class="panel-body">
            <table class="table table-hover">
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
                        <td><img alt="图片" src="${context}/seckill/img/seckill/${sk.seckillId}"
                                 style="width: 80px;height: 80px;"></td>
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
                            <a class="btn btn-primary" href="${context}/seckill/uploadPhoto/${sk.seckillId}"
                               target="_blank">上传图片</a>
                                <%--<a class="btn btn-info"  target="_blank" href="${context}/seckill/${sk.seckillId}/detail" onclick="checkExposer(${sk.seckillId})">链接</a>--%>
                            <a class="btn btn-danger" href="${context}/seckill/${sk.seckillId}/delete"
                               target="_blank">删除</a>
                            <a class="btn btn-info" href="${context}/seckill/${sk.seckillId}/detail"
                               onclick="checkExposer(${sk.seckillId})" target="_blank">活动链接</a>
                            <a class="btn btn-warning" href="${context}/seckill/${sk.seckillId}/edit"
                               target="_blank">修改</a>
                        </td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="7">
                        <nav aria-label="Page navigation">
                            <ul class="pagination">
                                <li>
                                    <a href="#" aria-label="Previous">
                                        <span aria-hidden="true">&laquo;</span>
                                    </a>
                                </li>
                                <c:forEach var="i" begin="1" end="${pageNum}">
                                    <li><a href="${context}/seckill/list?offset=${i}&limit=4">${i}</a></li>
                                </c:forEach>
                                <li>
                                    <a href="#" aria-label="Next">
                                        <span aria-hidden="true">&raquo;</span>
                                    </a>
                                </li>
                                <li>每页<input id="pageNum" type="text"/>条记录</li>
                                <li><input type="button" class="btn btn-primary" value="确定" onclick="changePageNum();">
                                </li>
                            </ul>
                        </nav>
                    </td>
                </tr>
                </tbody>
            </table>

        </div>
    </div>
</div>
</body>
</html>