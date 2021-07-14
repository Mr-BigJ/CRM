<%@ page pageEncoding="utf-8"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<%
	String basepath=request.getScheme()+"://"+request.getServerName()+":"+ request.getServerPort()+request.getContextPath()+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basepath%>"><%--http://localhost:8080/CRM/--%>
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
	<script type="text/javascript">
		$(function () {
			var lid=$("#loginId");
			lid.focus();
			//为当前页设置敲键盘事件
			$(window).keydown(function (event) {
				if(event.keyCode == 13){
					login();
                }
			})
            $("#b1").click(function () {
                login();
            })

		})
		//将重复性代码设置成一个函数，且不能放在$(function () { 里面
		function login() {
			var loginId=$("#loginId").val();
			var loginpwd=$("#loginpwd").val();
			if(loginId.trim() == "" ){
				$("#msg").html("<font color='red'>账号不能为空</font>")
				return false;
			}else if(loginpwd.trim() == ""){
				$("#msg").html("<font color='red'>密码不能为空</font>")
				return false;
			}
			//不返回false就走这
			$.ajax({
				//下面的url前面不能加/
				url:"settings/user/login.do",
				data:{
					//需要后端返回什么，要考虑清楚，是否登录成功{"success":"true/false","msg":"哪错了"}
					//登录成功就跳转，登录失败返回失败的详细信息
					"loginId":loginId,//上面拿了他们的value
					"loginpwd":loginpwd,
				},
				type:"post",
				dataType:"json",
				success:function (data){
					if(data.success){
						//跳转到欢迎页
						window.location.href="workbench/index.jsp"
					}else {
						//登录失败
						$("#msg").html("<font color='red'>"+data.msg+"</font>")
					}
				}
			})
		}


	</script>
</head>
<body>

	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2017&nbsp;动力节点</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="workbench/index.jsp" class="form-horizontal" role="form" id="loginForm" >
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" type="text" placeholder="用户名" id="loginId" name="loginId">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" type="password" placeholder="密码" id="loginpwd" name="loginpwd">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						
							<span id="msg"></span>
						
					</div>
					<button id="b1" type="button" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>