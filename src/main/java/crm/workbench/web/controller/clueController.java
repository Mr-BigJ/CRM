package crm.workbench.web.controller;

import crm.settings.domain.User;
import crm.utils.DateTimeUtil;
import crm.utils.PrintJson;
import crm.utils.ServiceFactory;
import crm.utils.UUIDUtil;
import crm.workbench.domain.Activity;
import crm.workbench.domain.Clue;
import crm.workbench.domain.Tran;
import crm.workbench.service.Impl.activityServiceImpl;
import crm.workbench.service.Impl.clueServiceImpl;
import crm.workbench.service.activityService;
import crm.workbench.service.clueService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class clueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入线索控制器");
        String path=request.getServletPath();
        if("/workbench/clue/selectUserList.do".equals(path)){
            userList(request,response);
        }else if("/workbench/clue/saveClue.do".equals(path)){
            saveClue(request,response);
        }else if("/workbench/clue/clueDetail.do".equals(path)){
            clueDetail(request,response);
        }else if("/workbench/clue/getActivityByClueId.do".equals(path)){
            getActivityByClueId(request,response);
        }else if("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);

        }else if("/workbench/clue/searchUnbundActivity.do".equals(path)){
            searchUnbundActivity(request,response);

        }else if("/workbench/clue/relateA.do".equals(path)){
            relateA(request,response);

        }else if("/workbench/clue/searchActivity.do".equals(path)){
            searchActivity(request,response);
        }
        else if("/workbench/clue/switchC.do".equals(path)){
            switchC(request,response);
        }
    }

    private void switchC(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String clueId=request.getParameter("clueId");
        Tran tran=new Tran();
        String activityId=request.getParameter("aid");
        String money=request.getParameter("money");
        String name=request.getParameter("tradeName");
        String expectedDate=request.getParameter("preDate");
        String stage=request.getParameter("stage");
        String id=UUIDUtil.getUUID();
        String createTime=DateTimeUtil.getSysTime();
        String createBy=((User)request.getSession(false).getAttribute("user")).getName();
        tran.setActivityId(activityId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setExpectedDate(expectedDate);
        tran.setMoney(money);
        tran.setName(name);
        tran.setId(id);
        tran.setStage(stage);
        clueService service= (clueService) ServiceFactory.getService(new clueServiceImpl());
        /*
        * 业务层传递的参数：
        *   1.必须传递的参数clueId 2.必须传递一个tran 3.createBy
        * 不能传递request，因为破坏了MVC分层
        * */
        boolean f=service.switchC(clueId,tran,createBy);
        response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");

    }

    private void searchActivity(HttpServletRequest request, HttpServletResponse response) {
        String name=request.getParameter("name");
        activityService service= (activityService) ServiceFactory.getService(new activityServiceImpl());
        List<Activity> alist=service.searchActivity(name);
        PrintJson.printJsonObj(response,alist);
    }

    private void relateA(HttpServletRequest request, HttpServletResponse response) {
        String[] aids=request.getParameterValues("aid");
        String cid=request.getParameter("cid");
        clueService service= (clueService) ServiceFactory.getService(new clueServiceImpl());
        boolean f=service.relateA(cid,aids);
        PrintJson.printJsonFlag(response,f);

    }

    private void searchUnbundActivity(HttpServletRequest request, HttpServletResponse response) {
        String name=request.getParameter("name");
        String clueId=request.getParameter("clueId");
        activityService service= (activityService) ServiceFactory.getService(new activityServiceImpl());
        Map<String,String> map=new HashMap<>();
        map.put("name",name);
        map.put("clueId",clueId);
        List<Activity> alist=service.searchUnbundActivity(map);
        PrintJson.printJsonObj(response,alist);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        String id=request.getParameter("id");
        clueService service= (clueService) ServiceFactory.getService(new clueServiceImpl());
        boolean f=service.unbund(id);
        PrintJson.printJsonFlag(response,f);
    }

    private void getActivityByClueId(HttpServletRequest request, HttpServletResponse response) {
        String clueId=request.getParameter("clueId");
        activityService activityService= (activityService) ServiceFactory.getService(new activityServiceImpl());
        List<Activity> alist=activityService.getActivityByClueId(clueId);
        PrintJson.printJsonObj(response,alist);
    }

    private void clueDetail(HttpServletRequest request, HttpServletResponse response) {
        String id=request.getParameter("id");
        clueService service= (clueService) ServiceFactory.getService(new clueServiceImpl());
        Clue clue=service.clueDetail(id);
        request.getSession(false).setAttribute("clue",clue);


    }

    private void saveClue(HttpServletRequest request, HttpServletResponse response) {
        String id= UUIDUtil.getUUID();
        String fullname=request.getParameter("fullname");
        String appellation=request.getParameter("appellation");
        String owner=request.getParameter("owner");
        String company=request.getParameter("company");
        String job=request.getParameter("job");
        String email=request.getParameter("email");
        String phone=request.getParameter("phone");
        String website=request.getParameter("website");
        String mphone=request.getParameter("mphone");
        String state=request.getParameter("state");
        String source=request.getParameter("source");
        User user= (User) request.getSession(false).getAttribute("user");
        String createBy=user.getName();
        String createTime= DateTimeUtil.getSysTime();
        String description=request.getParameter("description");
        String contactSummary=request.getParameter("contactSummary");
        String nextContactTime=request.getParameter("nextContactTime");
        String address=request.getParameter("address");
        Clue clue=new Clue();
        clue.setAddress(address);
        clue.setAppellation(appellation);
        clue.setCompany(company);
        clue.setContactSummary(contactSummary);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setEmail(email);
        clue.setFullname(fullname);
        clue.setId(id);
        clue.setJob(job);
        clue.setMphone(mphone);
        clue.setPhone(phone);
        clue.setNextContactTime(nextContactTime);
        clue.setOwner(owner);
        clue.setState(state);
        clue.setSource(source);
        clue.setWebsite(website);
        clueService service= (clueService) ServiceFactory.getService(new clueServiceImpl());
        boolean flag=service.saveClue(clue);
        PrintJson.printJsonFlag(response,flag);



    }

    private void userList(HttpServletRequest request, HttpServletResponse response) {
        activityService service= (activityService) ServiceFactory.getService(new activityServiceImpl());
        List<User> list=service.selectUserList();
        PrintJson.printJsonObj(response,list);
    }
}
