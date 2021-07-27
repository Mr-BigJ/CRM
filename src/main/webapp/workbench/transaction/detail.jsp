<%@ page import="crm.settings.domain.dic_value" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="crm.workbench.domain.Tran" %>
<%@page pageEncoding="utf-8"%>
<%
String path=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
//准备阶段,获取阶段值列表 01资质审查...
List<dic_value> stages= (List<dic_value>) application.getAttribute("stagelist");
//获取对应关系
Map<String,String> po= (Map<String, String>) application.getAttribute("possibility");
//准备map的keyset
Set<String> set=po.keySet();
//准备前面正常阶段和后面丢失阶段的分界点下标
int point=1;
for(int i=0;i<stages.size();i++){
	//从stages中获取每一条
	dic_value stage=stages.get(i);
	//获取每一条stage的值
	String stagevalue=stage.getValue();
	//根据stagevalue获取相应的可能性
	String possibility=po.get(stagevalue);
	//如果可能性为0，说明找到了前面正常阶段和丢失阶段的分界点
	if("0".equals(possibility)){
		point=i;
		break;
	}
}
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<!DOCTYPE html>
<html>
<head>
	<base href="<%=path%>">
	<meta charset="UTF-8">

	<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />

	<style type="text/css">
		.mystage{
			font-size: 20px;
			vertical-align: middle;
			cursor: pointer;
		}
		.closingDate{
			font-size : 15px;
			cursor: pointer;
			vertical-align: middle;
		}
	</style>

	<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
	<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

	<script type="text/javascript">
		//默认情况下取消和保存按钮是隐藏的
		var cancelAndSaveBtnDefault = true;

		$(function() {
			$("#remark").focus(function () {
				if (cancelAndSaveBtnDefault) {
					//设置remarkDiv的高度为130px
					$("#remarkDiv").css("height", "130px");
					//显示
					$("#cancelAndSaveBtn").show("2000");
					cancelAndSaveBtnDefault = false;
				}
			});

			$("#cancelBtn").click(function () {
				//显示
				$("#cancelAndSaveBtn").hide();
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height", "90px");
				cancelAndSaveBtnDefault = true;
			});

			$(".remarkDiv").mouseover(function () {
				$(this).children("div").children("div").show();
			});

			$(".remarkDiv").mouseout(function () {
				$(this).children("div").children("div").hide();
			});

			$(".myHref").mouseover(function () {
				$(this).children("span").css("color", "red");
			});

			$(".myHref").mouseout(function () {
				$(this).children("span").css("color", "#E6E6E6");
			});

			//阶段提示框
			$(".mystage").popover({
				trigger: 'manual',
				placement: 'bottom',
				html: 'true',
				animation: false
			}).on("mouseenter", function () {
				var _this = this;
				$(this).popover("show");
				$(this).siblings(".popover").on("mouseleave", function () {
					$(_this).popover('hide');
				});
			}).on("mouseleave", function () {
				var _this = this;
				setTimeout(function () {
					if (!$(".popover:hover").length) {
						$(_this).popover("hide")
					}
				}, 100);
			});

			searchHistory()
		})
		function searchHistory() {
			$.ajax({
				url:"workbench/transaction/searchHistory.do",
				type:"get",
				dataType:"json",
				data:{
					"tranId":"${trandetail.id}"
				},
				success:function (data) {
					//data：{{历史1},{2},{3}}
					var html="";
					$.each(data,function (i,n) {
						html+='<tr>';
						html+='<td>'+n.stage+'</td>';
						html+='<td>'+n.money+'</td>';
						html+='<td>'+n.possibility+'</td>';
						html+='<td>'+n.expectedDate+'</td>';
						html+='<td>'+n.createTime+'</td>';
						html+='<td>'+n.createBy+'</td>';
						html+='</tr>';
					})
					$("#tranHistoryBody").html(html)
				}
			})

		}

		function changestage(stage,i) {
			$.ajax({
				url:"workbench/transaction/changeStage.do",
				tpe:"get",
				dataType: "json",
				data:{
					"tranId":"${trandetail.id}",
					"stage":stage,
					"money":"${trandetail.money}",
					"expectedDate":"${trandetail.expectedDate}"
				},
				success:function (data) {
					//返回一个tran，给需要重新赋值的地方局部刷新
					$("#stage").html(data.stage)
					$("#possibility").html(data.possibility)
					$("#editTime").html(data.editTime)
					$("#editBy").html(data.editBy)

					//重新刷新历史记录列表
					searchHistory()

					//将所有的图标重新判断赋值
					changeIcon(stage,i)
				}
			})
		}
		function changeIcon(stage,i) {
			//当前阶段
			var currentStage=stage;
			//当前阶段下标
			var index=i;
			//当前阶段可能性
			var currentPo=$("#possibility").html();
			//前面正常阶段和丢失阶段的分界点下标
			var point="<%=point%>";

			if(currentPo=="0"){
				//当前阶段值为0，前7个一定是黑圈，后面一个黑叉一个红叉
				for(var ii=0;ii<point;ii++){
					//遍历前7个，黑圈
					$("#"+ii).removeClass();//删除样式
					$("#"+ii).addClass("glyphicon glyphicon-record mystage");//添加新样式
					$("#"+ii).css("color","#000000")//更改颜色

				}
				for(var ii=point;ii<<%=stages.size()%>;ii++){
					//遍历后两个，一个黑一个红
					if(index==ii){
						//当前阶段下标等于遍历的下标，红叉
						$("#"+ii).removeClass();//删除样式
						$("#"+ii).addClass("glyphicon glyphicon-remove mystage");//添加新样式
						$("#"+ii).css("color","#ff0000")//更改颜色
					}else {
						//黑叉
						$("#"+ii).removeClass();//删除样式
						$("#"+ii).addClass("glyphicon glyphicon-remove mystage");//添加新样式
						$("#"+ii).css("color","#000000")//更改颜色
					}
				}
			}else {
				//前7个可能是黑圈，绿圈，绿标，后两个一定是黑叉
				for(var ii=0;ii<point;ii++){
					//遍历前7个，如果当前阶段下标和遍历的阶段下标相等，绿标
					if(index==ii){
						$("#"+ii).removeClass();//删除样式
						$("#"+ii).addClass("glyphicon glyphicon-map-marker mystage");//添加新样式
						$("#"+ii).css("color","#00ff00")//更改颜色
					}else if(index<ii){
						//如果当前阶段下标小于ii，黑圈
						$("#"+ii).removeClass();//删除样式
						$("#"+ii).addClass("glyphicon glyphicon-record mystage");//添加新样式
						$("#"+ii).css("color","#000000")//更改颜色
					}else {
						//大于。绿圈
						$("#"+ii).removeClass();//删除样式
						$("#"+ii).addClass("glyphicon glyphicon-ok-circle mystage");//添加新样式
						$("#"+ii).css("color","#00ff00")//更改颜色
					}
				}
				//遍历后两个，黑叉
				for(var ii=point;ii<<%=stages.size()%>;ii++){
					$("#"+ii).removeClass();//删除样式
					$("#"+ii).addClass("glyphicon glyphicon-remove mystage");//添加新样式
					$("#"+ii).css("color","#000000")//更改颜色
				}
			}
		}

	</script>

</head>
<body>

<!-- 返回按钮 -->
<div style="position: relative; top: 35px; left: 10px;">
	<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
</div>

<!-- 大标题 -->
<div style="position: relative; left: 40px; top: -30px;">
	<div class="page-header">
		<h3>${trandetail.name}-${trandetail.createTime} <small>￥${trandetail.money}</small></h3>
	</div>
	<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
		<button type="button" class="btn btn-default" onclick="window.location.href='edit.jsp';"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
		<button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
	</div>
</div>

<!-- 阶段状态 -->
<div style="position: relative; left: 40px; top: -50px;">
	阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<%

		//取当前的阶段和当前阶段值
		Tran tran= (Tran) request.getAttribute("trandetail");
		String currentStage=tran.getStage();
		String currentValue=po.get(currentStage);


		//判断的逻辑,如果当前阶段的可能性是0，前7个一定是黑圈，后两个一个是红叉，一个黑叉
		if("0".equals(currentValue)){
			//遍历所有阶段，当查出哪个阶段是0的时候，就标记红叉
			for(int i=0;i<stages.size();i++){
				dic_value s=stages.get(i);
				String stage=s.getValue();
				String v=po.get(stage);//每一个可能性
				if(currentStage.equals(stage) && currentValue.equals(v)){
					//当匹配到当前阶段时，标记红叉
	%>
				<span id="<%=i%>" onclick="changestage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
					  data-toggle="popover" data-placement="bottom"
					  data-content="<%=stage%>" style="color: #ff0000;"></span>
				-----------

	<%
				}else if(!currentStage.equals(stage) && "0".equals(v)){
					//黑叉
	%>
				<span id="<%=i%>" onclick="changestage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
					  data-toggle="popover" data-placement="bottom"
					  data-content="<%=stage%>" style="color: #000000;"></span>
				-----------
	<%
				}else {
					//7个黑圈

	%>
				<span id="<%=i%>" onclick="changestage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-record mystage"
					  data-toggle="popover" data-placement="bottom"
					  data-content="<%=stage%>" style="color: #000000;"></span>
				-----------
	<%

				}
			}
		}else {


			//如果不是0，前面7个有绿标，绿圈和黑圈，后面两个一定是黑叉
			for(int i=0;i<stages.size();i++){
				dic_value s=stages.get(i);
				String stage=s.getValue();
				String v=po.get(stage);//每一个可能性
				if(currentStage.equals(stage) && currentValue.equals(v)){
					//当匹配到当前阶段时，标记绿标
	%>
				<span id="<%=i%>" onclick="changestage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-map-marker mystage"
					  data-toggle="popover" data-placement="bottom"
					  data-content="<%=stage%>" style="color: #00ff00;"></span>
				-----------
	<%
				}else if(!stage.equals(currentStage) && !"0".equals(v)){
					//不是当前阶段且这些阶段值不为0时，是黑圈或者是绿圈
					//比较值，后面阶段值肯定比前面的大
					int cv=Integer.parseInt(currentValue);
					int ev=Integer.parseInt(v);
					if(cv>ev){
						//如果当前阶段的值大于遍历的阶段值，遍历的阶段都是绿圈
	%>
					<span id="<%=i%>" onclick="changestage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-ok-circle mystage"
						  data-toggle="popover" data-placement="bottom"
						  data-content="<%=stage%>" style="color: #00ff00;"></span>
					-----------
	<%
					}else {
						//小于当前阶段值，其他阶段都是黑圈
	%>
					<span id="<%=i%>" onclick="changestage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-record mystage"
						  data-toggle="popover" data-placement="bottom"
						  data-content="<%=stage%>" style="color: #000000;"></span>
					-----------
	<%
					}
				}else if("0".equals(v)){
					//黑叉
	%>
				<span id="<%=i%>" onclick="changestage('<%=stage%>','<%=i%>')" class="glyphicon glyphicon-remove mystage"
					  data-toggle="popover" data-placement="bottom"
					  data-content="<%=stage%>" style="color: #000000;"></span>
				-----------
	<%
				}
			}
		}
	%>
	<%--<span class="glyphicon glyphicon-ok-circle mystage" data-toggle="popover" data-placement="bottom" data-content="资质审查" style="color: #90F790;"></span>
	-----------
	--%>
	<span class="closingDate">2010-10-10</span>
</div>

<!-- 详细信息 -->
<div style="position: relative; top: 0px;">
	<div style="position: relative; left: 40px; height: 30px;">
		<div style="width: 300px; color: gray;">所有者</div>
		<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${trandetail.owner}</b></div>
		<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
		<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${trandetail.money}</b></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 10px;">
		<div style="width: 300px; color: gray;">名称</div>
		<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${trandetail.name}</b></div>
		<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
		<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${trandetail.expectedDate}</b></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 20px;">
		<div style="width: 300px; color: gray;">客户名称</div>
		<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${trandetail.customerId}</b></div>
		<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>
		<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="stage">${trandetail.stage}</b></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 30px;">
		<div style="width: 300px; color: gray;">类型</div>
		<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${trandetail.type}</b></div>
		<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">可能性</div>
		<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="possibility">${trandetail.possibility}</b></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 40px;">
		<div style="width: 300px; color: gray;">来源</div>
		<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${trandetail.source}&nbsp;&nbsp;</b></div>
		<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
		<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${trandetail.activityId}&nbsp;&nbsp;</b></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 50px;">
		<div style="width: 300px; color: gray;">联系人名称</div>
		<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${trandetail.contactsId}</b></div>
		<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 60px;">
		<div style="width: 300px; color: gray;">创建者</div>
		<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${trandetail.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${trandetail.createTime}</small></div>
		<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 70px;">
		<div style="width: 300px; color: gray;">修改者</div>
		<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b id="editBy">${trandetail.editBy}&nbsp;&nbsp;</b><small id="editTime" style="font-size: 10px; color: gray;">${trandetail.editTime}</small></div>
		<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 80px;">
		<div style="width: 300px; color: gray;">描述</div>
		<div style="width: 630px;position: relative; left: 200px; top: -20px;">
			<b>
				${trandetail.description}
			</b>
		</div>
		<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 90px;">
		<div style="width: 300px; color: gray;">联系纪要</div>
		<div style="width: 630px;position: relative; left: 200px; top: -20px;">
			<b>
				&nbsp;${trandetail.contactSummary}
			</b>
		</div>
		<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
	</div>
	<div style="position: relative; left: 40px; height: 30px; top: 100px;">
		<div style="width: 300px; color: gray;">下次联系时间</div>
		<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>&nbsp;${trandetail.nextContactTime}</b></div>
		<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
	</div>
</div>

<!-- 备注 -->
<div style="position: relative; top: 100px; left: 40px;">
	<div class="page-header">
		<h4>备注</h4>
	</div>

	<!-- 备注1 -->
	<div class="remarkDiv" style="height: 60px;">
		<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
		<div style="position: relative; top: -40px; left: 40px;" >
			<h5>哎呦！</h5>
			<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
			<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
				<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
			</div>
		</div>
	</div>

	<!-- 备注2 -->
	<div class="remarkDiv" style="height: 60px;">
		<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
		<div style="position: relative; top: -40px; left: 40px;" >
			<h5>呵呵！</h5>
			<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>
			<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
				<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
			</div>
		</div>
	</div>

	<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
		<form role="form" style="position: relative;top: 10px; left: 10px;">
			<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
			<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
				<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
				<button type="button" class="btn btn-primary">保存</button>
			</p>
		</form>
	</div>
</div>

<!-- 阶段历史 -->
<div>
	<div style="position: relative; top: 100px; left: 40px;">
		<div class="page-header">
			<h4>阶段历史</h4>
		</div>
		<div style="position: relative;top: 0px;">
			<table id="activityTable" class="table table-hover" style="width: 900px;">
				<thead>
				<tr style="color: #B3B3B3;">
					<td>阶段</td>
					<td>金额</td>
					<td>可能性</td>
					<td>预计成交日期</td>
					<td>创建时间</td>
					<td>创建人</td>
				</tr>
				</thead>
				<tbody id="tranHistoryBody">
					<%--<tr>
						<td>资质审查</td>
						<td>5,000</td>
						<td>10</td>
						<td>2017-02-07</td>
						<td>2016-10-10 10:10:10</td>
						<td>zhangsan</td>
					</tr>--%>

				</tbody>
			</table>
		</div>

	</div>
</div>

<div style="height: 200px;"></div>

</body>
</html>