<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@include file="../common/tag.jsp" %>
<%@include file="../common/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>用户列表页</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script type="application/javascript">
        function changePageNum() {
            var pageNum=$("#pageNum").val();
            window.location.href="${context}/seckill/admin/user?limit="+pageNum;
        }
    </script>
</head>
<body>

<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading text-center">
            <h2 class="text-primary">用户列表</h2>
        </div>
        <div class="panel-body">
            <table class="table table-hover table-striped">
                <thead>
                <tr>
                    <th>用户账号</th>
                    <th>用户名称</th>
                    <th>创建时间</th>
                    <th>更新时间</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="sk" items="${list}">
                    <tr>
                        <td>${sk.account}</td>
                        <td>${sk.username}</td>
                        <td>
                            <fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
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
                                    <li><a href="${context}/seckill/admin/user?offset=${i}&limit=4">${i}</a></li>
                                </c:forEach>
                                <li>
                                    <a href="#" aria-label="Next">
                                        <span aria-hidden="true">&raquo;</span>
                                    </a>
                                </li>
                                <li>每页<input class="" id="pageNum" type="text"/>条记录</li>
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