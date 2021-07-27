<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/7/26
  Time: 21:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page pageEncoding="utf-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <%                      //http               localhost                          8080              /项目名
String basepath=request.getScheme()+"://"+request.getServerName()+":"+ request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
    <base href="<%=basepath%>">
    <title>Title</title>
    <script src="jquery/jquery-1.11.1-min.js"></script>
    <script src="echarts/echarts.min.js"></script>
    <script type="text/javascript">
        $(function () {
            // 基于准备好的dom，初始化echarts实例
            var myChart = echarts.init(document.getElementById('main'));

            // 指定图表的配置项和数据
            var option = {
                title: {
                    text: '交易阶段统计图',
                    subtext: '纯属虚构',
                    top: 10,
                    left: 10
                },
                /*tooltip: {
                    trigger: 'item'
                },*/

                //这个是下面的年份
                legend: {
                    type: 'scroll',
                    bottom: 10,
                    //年份
                    data: (function (){
                        var list = [];
                        for (var i = 1; i <=28; i++) {
                            list.push('张'+i);//i + 2000 + '');
                        }
                        return list;
                    })()
                },

                //颜色,和侧边的条
                visualMap: {
                    top: 'middle',
                    right: 10,
                    color: ['red', 'yellow'],
                    calculable: true
                },

                //统计项和最大值
                radar: {
                    indicator: [
                        { text: 'IE8-', max: 400},
                        { text: 'IE9+', max: 400},
                        { text: 'Safari', max: 400},
                        { text: 'Firefox', max: 400},
                        { text: 'Chrome', max: 400}
                    ]
                },

                series: (function (){
                    var series = [];
                    for (var i = 1; i <= 28; i++) {
                        series.push({
                            name: '浏览器（数据纯属虚构）',
                            type: 'radar',
                            symbol: 'none',
                            lineStyle: {
                                width: 1
                            },
                            emphasis: {
                                areaStyle: {
                                    color: 'rgba(0,250,0,0.3)'
                                }
                            },
                            data: [{
                                value: [
                                    /*(40 - i) * 10,
                                    (38 - i) * 4 + 60,
                                    i * 5 + 10,
                                    i * 9,
                                    i * i /2*/
                                    80,
                                    100,
                                    90,
                                    99

                                ],
                                name:'张'+i                 //i + 2000 + ''
                            }]
                        });
                    }
                    return series;
                })()
            };

            // 使用刚指定的配置项和数据显示图表。
            myChart.setOption(option);
        })


    </script>
</head>
<body>
    <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
    <div id="main" style="width: 600px;height:400px;"></div>
</body>
</html>
