<%@page pageEncoding="utf-8"%>
<%
String path=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<!DOCTYPE html>
<html>
<head>
	<base href="<%=path%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<%--	引入分页插件--%>
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.css"></script>
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

<script type="text/javascript">

	$(function(){
		//删除市场活动的操作
		$("#deleteBtn").click(function () {
			//找到复选框中所有打钩的jQuery对象
			var cbox=$("input[name=comcheck]:checked");
			if(cbox.length==0){
				alert("请选择要删除的市场活动")
			}else {
				//需要将选中的框的id拼起来，用.com?id=?&id=?&..的方式
				var param="";
				//将cbox中的每个dom对象遍历取出value，相当于取得了要删除的记录的id
				for(var i=0;i<cbox.length;i++){
					param+="id="+$(cbox[i]).val();
					//如果不是最后一个元素，需要在后面加个&
					if(i<cbox.length-1){
						param+="&";
					}
				}
				if(confirm("是否确定删除？")){
					$.ajax({
						url:"workbench/activity/deleteActivity.do",
						dataType:"json",
						data:param,
						type:"get",
						success:function (){
							//这个没用，初步猜想，有返回参数且经过判断才会执行success中的方法
							//searchActivityList(1,2);
						}
					})
				}
				//分页插件提供的，可以保持用户修改的展示条数，分页组件点击后可以保持回到当前页
				//第一个参数是回到哪页，第二个是保持展示条数
				/*pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
						,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
*/
				searchActivityList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));


			}
		})


		//动态生成的活动列表复选框的勾选
		$("#totalcheck").click(function () {
			//prop方法，设置或返回被选元素的属性和值
			$("input[name=comcheck]").prop("checked",this.checked)
		})
		//对于动态生成的表单元素不能使用普通的绑定事件，要在有效外层绑
		$("#activityShow").on("click",$("input[name=comcheck]"),function () {
			$("#totalcheck").prop("checked",$("input[name=comcheck]").length==$("input[name=comcheck]:checked").length)
		})


		//分页查询，展示活动信息列表,调用searchActivityList方法
		/*
		* 查询入口：1-保存新增的活动信息自动刷新
		* 		   2-点击市场活动导航
		* 		   3-查询条件4个
		* 			4-修改后点击更新
		* */
		//当页面加载完毕就查询相当于点击市场导航
		searchActivityList(1,2);

		//按下查询后执行
		$("#searchBtn").click(function () {
			/*
			* 使用定好的隐藏域保存搜索信息，点击查询时将查询的信息保存起来，
			* 在函数内将保存的信息取出
			* 每次点击查询后，查询信息自动保存，在不重新点击查询前，下一页等组件都是按照保存好的搜索信息
			* 查询
			* */
			$("#hidden-name").val($.trim($("#search-name").val()))
			$("#hidden-owner").val($.trim($("#search-owner").val()))
			$("#hidden-startDate").val($.trim($("#search-startDate").val()))
			$("#hidden-endDate").val($.trim($("#search-endDate").val()))
			searchActivityList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
		})

		//弹出修改框时要查询信息填充修改框
        $("#editBtn").click(function () {

            var $cbox=$("input[name=comcheck]:checked");
            if($cbox.length==0){
                alert("请选择需要修改的活动信息")
            }else if($cbox.length>1){
                alert("一次只能修改一条记录")
            }else {
				$("#editActivityModal").modal("show");
                //id是当前勾选的活动的id,因为现在只能是一个处于勾选状态，所有$cbox就只有一个dom对象
                var id=$cbox.val();
                $.ajax({
                    url:"workbench/activity/editActivity.do",
                    dataType: "json",
                    data:{
                        "id":id,
                    },
                    type: "post",
                    success:function (data) {

                        $("#edit-Owner").html("<option>"+data.owner+"</option>")
                        $("#edit-Name").val(data.name)
						$("#edit-Name").html("<input id='hiddenid' type='hidden' value='"+data.id+"' />")
                        $("#edit-startDate").val(data.startDate)
                        $("#edit-endDate").val(data.endDate)
                        $("#edit-cost").val(data.cost)
                        $("#edit-description").val(data.description)
                    }
                })
            }

        })

		//按下更新后执行
		$("#updateBtn").click(function () {
			$.ajax({
				url:"workbench/activity/updateActivity.do",
				dataType: "json",
				data:{
					"id":$("#hiddenid").val(),
					"owner":$("#edit-Owner").val(),
					"name":$("#edit-Name").val(),
					"startDate":$("#edit-startDate").val(),
					"endDate":$("#edit-endDate").val(),
					"cost":$("#edit-cost").val(),
					"description":$("#edit-description").val(),
				},
				type: "post",
				success:function (data) {
					if(data){
						//成功,关闭模态窗口，并且清空窗口的信息
						$("#editActivityModal").modal("hide");
						//jQuery转dom对象,jquery中没有表单的reset方法，需要使用原生js的dom对象
						$("#editForm")[0].reset;
						//修改后回到当前页
						searchActivityList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

					}else {
						//失败
						alert("修改活动信息失败")
					}
				}
			})

		})



		//保存活动信息
		$("#saveBtn").click(function () {
			$.ajax({
				url:"workbench/activity/saveActivity.do",
				dataType: "json",
				data:{
					"owner":$("#create-Owner").val(),
					"name":$("#create-marketActivityName").val(),
					"startDate":$("#create-startDate").val(),
					"endDate":$("#create-endDate").val(),
					"cost":$("#create-cost").val(),
					"description":$("#create-description").val(),

				},
				type: "post",
				success:function (data) {
					if(data){
						//成功,关闭模态窗口，并且清空窗口的信息
						$("#createActivityModal").modal("hide");
						//jQuery转dom对象,jquery中没有表单的reset方法，需要使用原生js的dom对象
						$("#addActivityForm")[0].reset;
						searchActivityList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
					}else {
						//失败
						alert("插入活动信息失败")
					}
				}
			})


		})
		
		//添加日历插件,在需要使用插件的地方添加class=time
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});


		$("#addBtn").click(function () {
			//弹出模态窗口前，查询用户列表
			$.ajax({
				url:"workbench/activity/selectUserList.do",
				data:{

				},
				dataType:"json",
				type:"get",
				success:function (data) {
					var html="<option></option>";
					//遍历出来的每一个n，就是user对象
					$.each(data,function (i,n) {
						html+="<option value='"+n.id+"'>"+n.name+"</option>";
						$("#create-Owner").html(html)

						//下面这个方式不行。只能出现一个
						//$("#create-marketActivityOwner").html("<option value='"+n.id+"'>"+n.name+"</option>");
					})
					var id="${user.id}";
					$("#create-Owner").val(id)
				}
			})
			//从session中取得用户id，下拉列表默认是当前用户
			//el表达式在js中必须套在字符串中

			$("#createActivityModal").modal("show");
		})


	});

	function searchActivityList(pageNo,pagesize) {
		//每次执行这个方法时，把全选的复选框的√取消
		$("#totalcheck").prop("checked",false);

		//查询前，将保存的查询信息重新赋值到搜索框
		$("#search-name").val($.trim($("#hidden-name").val()))
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		$.ajax({
			url:"workbench/activity/searchActivityList.do",
			dataType:"json",
			data:{
				//必须的两个参数pagesize和pageno
				"pagesize":pagesize,
				"pageNo":pageNo,
				"search-name":$("#search-name").val(),
				"search-owner":$("#search-owner").val(),
				"search-startDate":$("#search-startDate").val(),
				"search-endDate":$("#search-endDate").val(),

			},
			type:"get",
			success:function (data) {
				//需要拿什么：1.总条数 2.一个存放活动信息的列表  格式应该是{"total":?,"datalist":{name,owner...}}
				$("#totalshow").html(data.totalsize);
				var html="";
				$.each(data.datalist,function (i,n) {
					html+='<tr class="active">';
					html+='<td><input type="checkbox" name="comcheck" value="'+n.id+'"/></td>';
					html+='<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html+='<td>'+n.owner+'</td>';
					html+='<td>'+n.startDate+'</td>';
					html+='<td>'+n.endDate+'</td>';
					html+='</tr>';
				})
				$("#activityShow").html(html);

				//计算总页数
				var totalPages=data.totalsize%pagesize==0 ? data.totalsize/pagesize : parseInt(data.totalsize/pagesize)+1
				//使用分页插件,在ajax的回调函数中
				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pagesize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.totalsize, // 总记录条数
					// 自己的参数只有pageNo  pagesize totalPages data.totalsize
					visiblePageLinks: totalPages, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//在点击分页组件的时候触发，包括上一页下一页，跳到，..
					onChangePage : function(event, data){
						searchActivityList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})



	}
	
</script>
</head>
<body>
<%--隐藏域，保存搜索栏的信息--%>
<input type="hidden" id="hidden-name">
<input type="hidden" id="hidden-owner">
<input type="hidden" id="hidden-startDate">
<input type="hidden" id="hidden-endDate">
	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="addActivityForm">
					
						<div class="form-group">
							<label for="create-Owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-Owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" readonly>
							</div>
							<label for="create-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="editForm">
					
						<div class="form-group">
							<label for="edit-Owner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-Owner">

								</select>
							</div>
                            <label for="edit-Name" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-Name" >
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startDate" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate" >
							</div>
							<label for="edit-endDate" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" >
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control time" type="text" id="search-startDate" readonly/>
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control time" type="text" id="search-endDate" readonly/>
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="searchBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="totalcheck"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityShow">


					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;" >
				<div id="activityPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>