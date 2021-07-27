package crm.workbench.web.controller;


import crm.settings.domain.User;
import crm.utils.*;
import crm.vo.queryVO;
import crm.workbench.domain.Activity;
import crm.workbench.domain.ActivityRemark;
import crm.workbench.service.Impl.activityServiceImpl;
import crm.workbench.service.activityService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class activityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入activity控制器");
        String path=request.getServletPath();
        if("/workbench/activity/selectUserList.do".equals(path)){
            selectUserList(request,response);
        }else if("/workbench/activity/saveActivity.do".equals(path)){
            saveActivity(request,response);
        }else if("/workbench/activity/searchActivityList.do".equals(path)){
            searchActivityList(request,response);
        }else if("/workbench/activity/deleteActivity.do".equals(path)){
            deleteActivity(request,response);
        }else if("/workbench/activity/updateActivity.do".equals(path)){
            updateActivity(request,response);
        }else if("/workbench/activity/editActivity.do".equals(path)){
            editActivity(request,response);
        }else if("/workbench/activity/detail.do".equals(path)){
            activityDetail(request,response);
        }else if("/workbench/activity/remarkList.do".equals(path)){
            remarkList(request,response);
        }else if("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        }else if("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }
    }

    private ActivityRemark reShowRemark(String id) {
        activityService service= (activityService) ServiceFactory.getService(new activityServiceImpl());
        ActivityRemark remark=service.reShowRemark(id);
        return remark;
    }
    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        String noteContent=request.getParameter("noteContent");
        String activityId=request.getParameter("activityId");
        String remarkId=UUIDUtil.getUUID();
        String editflag="0";
        String createTime=DateTimeUtil.getSysTime();
        User user=(User)request.getSession(false).getAttribute("user");
        String createBy=user.getName();
        ActivityRemark ar=new ActivityRemark();
        ar.setActivityId(activityId);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editflag);
        ar.setId(remarkId);
        ar.setNoteContent(noteContent);
        activityService service= (activityService) ServiceFactory.getService(new activityServiceImpl());
        int ac=service.saveRemark(ar);
        ActivityRemark remark=reShowRemark(remarkId);
        PrintJson.printJsonObj(response,remark);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        String id=request.getParameter("id");
        activityService service= (activityService) ServiceFactory.getService(new activityServiceImpl());
        int count=service.deleteRemark(id);
        if(count==1){
            PrintJson.printJsonFlag(response,true);
        }else {
            PrintJson.printJsonFlag(response,false);
        }
    }

    private void remarkList(HttpServletRequest request, HttpServletResponse response) {
        String aid=request.getParameter("id");
        activityService service= (activityService) ServiceFactory.getService(new activityServiceImpl());
        List<ActivityRemark> remarks=service.remarkList(aid);
        PrintJson.printJsonObj(response,remarks);
    }

    private void activityDetail(HttpServletRequest request, HttpServletResponse response) {
        String id=request.getParameter("id");
        activityService service= (activityService) ServiceFactory.getService(new activityServiceImpl());
        Activity activity=service.activityDetail(id);
        request.setAttribute("activity",activity);
        try {
            request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editActivity(HttpServletRequest request, HttpServletResponse response) {
        String id=request.getParameter("id");
        activityService service= (activityService) ServiceFactory.getService(new activityServiceImpl());
        Activity activity=service.editActivity(id);
        PrintJson.printJsonObj(response,activity);
    }

    private void updateActivity(HttpServletRequest request, HttpServletResponse response) {
        String id=request.getParameter("id"); //活动id
        String name=request.getParameter("name");
        String owner=request.getParameter("owner"); //user id
        String startDate=request.getParameter("startDate");
        String endDate=request.getParameter("endDate");
        String cost=request.getParameter("cost");
        String description=request.getParameter("description");
        activityService service=(activityService) ServiceFactory.getService(new activityServiceImpl());
        Activity activity=new Activity();
        activity.setId(id);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setOwner(owner);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setEditTime(DateTimeUtil.getSysTime());
        User user=(User) request.getSession(false).getAttribute("user");
        activity.setEditBy(user.getName());
        int count=service.updateActivity(activity);
        if(count == 1){
            PrintJson.printJsonFlag(response,true);
        }else {
            PrintJson.printJsonFlag(response,false);
        }

    }

    private void deleteActivity(HttpServletRequest request, HttpServletResponse response) {
        String[] ids=request.getParameterValues("id");
        //在删除活动信息之前，需要把该活动信息的活动备注表相应的备注删除，
        // 因此要有对应remark表的dao
        activityService service=(activityService) ServiceFactory.getService(new activityServiceImpl());
        int count=service.deleteActivityandRemark(ids);


    }

    private void searchActivityList(HttpServletRequest request, HttpServletResponse response) {
        String pagesizestr=request.getParameter("pagesize");
        String pageNostr=request.getParameter("pageNo");
        String name=request.getParameter("search-name");
        String owner=request.getParameter("search-owner");
        String startDate=request.getParameter("search-startDate");
        String endDate=request.getParameter("search-endDate");
        //设置limit的第一个参数，即过滤的条数
        int pagesize=Integer.parseInt(pagesizestr);
        int pageNo=Integer.parseInt(pageNostr);
        int filterCount=(pageNo-1)*pagesize;

        //打包数据给service，再给dao
        Map<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("pagesize",pagesize);
        map.put("pageNo",pageNo);
        map.put("filterCount",filterCount);
        activityService service=(activityService) ServiceFactory.getService(new activityServiceImpl());
        //查询市场活动,返回vo ,在service中的searchActivityList方法执行两次dao的查询，把查询结果都封装到vo中
        queryVO<Activity> vo=service.searchActivityList(map);
        PrintJson.printJsonObj(response,vo);

    }

    private void selectUserList(HttpServletRequest request, HttpServletResponse response){
        activityService service=(activityService)ServiceFactory.getService(new activityServiceImpl());
        List<User> ulist=service.selectUserList();
        PrintJson.printJsonObj(response,ulist);
    }

    private void saveActivity(HttpServletRequest request, HttpServletResponse response){
        User user=(User) request.getSession(false).getAttribute("user");
        String id= UUIDUtil.getUUID();
        String owner=request.getParameter("owner");//这个是下拉列表选中的人的id
        String name=request.getParameter("name");
        String startDate=request.getParameter("startDate");
        String endDate=request.getParameter("endDate");
        String cost=request.getParameter("cost");
        String description=request.getParameter("description");
        String createTime= DateTimeUtil.getSysTime();
        String createBy=user.getName();
        activityService service=(activityService) ServiceFactory.getService(new activityServiceImpl());

        Activity activity=new Activity();
        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);
        int count=service.saveActivity(activity);
        if(count == 1){
            PrintJson.printJsonFlag(response,true);
        }else {
            PrintJson.printJsonFlag(response,false);
        }

    }


}
