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
         function loadRoles() {
            $.ajax({
                url: "${context}/seckill/admin/role",
                type: "post",
                success: function (result) {
                    console.log(result[0]);
                    console.log($("#roleList tbody").after("<tr><td></td><td>"+result[0].roleName+"</td><td>"+result[0].updateTime+"</td><td><td></tr>"));
                }
            });
        }
    </script>
</head>
<body>

<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading text-center">
            <h2 class="text-primary">用户列表</h2>
        </div>
        <%--<div style="margin-top: 15px;margin-left: 15px">--%>
            <%--<a class="btn btn-info" href="${context}/seckill/admin/userRole" onclick="" target="_blank">管理用户角色</a>--%>
        <%--</div>--%>
        <button class="btn btn-primary" style="margin-top: 15px;margin-left: 15px" data-toggle="modal" data-target="#myModal" onclick="loadRoles();">
            管理用户角色
        </button>
        <!-- 模态框（Modal） -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                            &times;
                        </button>
                        <h4 class="modal-title" id="myModalLabel">
                            分配角色
                        </h4>
                    </div>
                    <div class="modal-body">
                        <table id="roleList" data-toggle="table" data-height="480" data-striped="true" data-click-to-select="true">
                            <thead>
                            <tr>
                                <th data-checkbox="true"></th>
                                <th>角色名称</th>
                                <th>创建时间</th>
                                <th>更新时间</th>
                            </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭
                        </button>
                        <button type="button" class="btn btn-primary">
                            提交更改
                        </button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal -->
        </div>
        <div class="panel-body">
            <table data-toggle="table" data-pagination="true" data-height="480" data-striped="true" data-page-list="[5, 10, 25, 50, 100, All]" data-click-to-select="true">
                <thead>
                <tr>
                    <th data-checkbox="true"></th>
                    <th>用户账号</th>
                    <th>用户名称</th>
                    <th>创建时间</th>
                    <th>更新时间</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="sk" items="${list}">
                    <tr>
                        <td></td>
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
                </tbody>
            </table>

        </div>
    </div>
</div>
</body>
</html>