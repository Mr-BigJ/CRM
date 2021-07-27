package crm.workbench.web.controller;

import crm.settings.domain.User;
import crm.utils.DateTimeUtil;
import crm.utils.PrintJson;
import crm.utils.ServiceFactory;
import crm.utils.UUIDUtil;
import crm.workbench.domain.Tran;
import crm.workbench.domain.TranHistory;
import crm.workbench.service.Impl.TranServiceImpl;
import crm.workbench.service.TranService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path=request.getServletPath();
        if("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }else if("/workbench/transaction/possibility.do".equals(path)){
            possibility(request,response);
        }else if("/workbench/transaction/saveTran.do".equals(path)){
            saveTran(request,response);
        } else if("/workbench/transaction/getTranDetail.do".equals(path)){
            getTranDetail(request,response);
        } else if("/workbench/transaction/searchHistory.do".equals(path)){
            searchHistory(request,response);
        } else if("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        String tranId=request.getParameter("tranId");
        String stage=request.getParameter("stage");
        String money=request.getParameter("money");
        String expectedDate=request.getParameter("expectedDate");
        TranService service= (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t=new Tran();
        t.setId(tranId);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(((User)request.getSession(false).getAttribute("user")).getName());
        t.setEditTime(DateTimeUtil.getSysTime());
        Tran t1=service.changeStage(t);
        Map<String,String> p= (Map<String, String>) request.getServletContext().getAttribute("possibility");
        String poss=p.get(t1.getStage());
        t1.setPossibility(poss);
        PrintJson.printJsonObj(response,t1);
    }

    private void searchHistory(HttpServletRequest request, HttpServletResponse response) {
        String tranId=request.getParameter("tranId");
        TranService service= (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> hlist=service.searchHistory(tranId);
        Map<String,String> po= (Map<String, String>) request.getServletContext().getAttribute("possibility");
        for(TranHistory h:hlist){
            String value=po.get(h.getStage());
            h.setPossibility(value);
        }
        PrintJson.printJsonObj(response,hlist);
    }

    private void getTranDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tranId=request.getParameter("tranId");
        TranService service= (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran tran=service.getTranDetail(tranId);
        //取可能性
        Map<String,String> po= (Map<String, String>) request.getServletContext().getAttribute("possibility");
        String value=po.get(tran.getStage());
        tran.setPossibility(value);
        request.setAttribute("trandetail",tran);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);

    }

    private void saveTran(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = UUIDUtil.getUUID();
        String owner=request.getParameter("owner");
        String money=request.getParameter("money");
        String name=request.getParameter("name");
        String expectedDate=request.getParameter("expectedDate");
        //String customerId=request.getParameter("");
        String stage=request.getParameter("stage");
        String type=request.getParameter("type");
        String source=request.getParameter("source");
        String activityId=request.getParameter("aid");
        String contactsId=request.getParameter("conid");
        String createBy=((User)request.getSession(false).getAttribute("user")).getName();
        String createTime= DateTimeUtil.getSysTime();
        String description=request.getParameter("description");
        String contactSummary=request.getParameter("contactSummary");
        String nextContactTime=request.getParameter("nextContactTime");
        String customerName=request.getParameter("customerName");
        Tran tran=new Tran();
        tran.setType(type);
        tran.setActivityId(activityId);
        tran.setCreateBy(createBy);
        tran.setExpectedDate(expectedDate);
        tran.setCreateTime(createTime);
        tran.setMoney(money);
        tran.setName(name);
        tran.setId(id);
        tran.setStage(stage);
        tran.setSource(source);
        tran.setOwner(owner);
        tran.setNextContactTime(nextContactTime);
        tran.setDescription(description);
        tran.setContactsId(contactsId);
        tran.setContactSummary(contactSummary);
        TranService service= (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean f=service.saveTran(tran,customerName);
        if(f){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }

    }

    private void possibility(HttpServletRequest request, HttpServletResponse response) {
        ResourceBundle resourceBundle=ResourceBundle.getBundle("possibility");
        Enumeration<String> keys=resourceBundle.getKeys();
        Map<String,String> map=new HashMap<>();

        while (keys.hasMoreElements()){
            String key=keys.nextElement();
            map.put(key,resourceBundle.getString(key));

        }

        PrintJson.printJsonObj(response,map);
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        String name=request.getParameter("name");
        TranService service= (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<String> list=service.getCustomerName(name);
        PrintJson.printJsonObj(response,list);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TranService service= (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<User> ulist=service.save();
        request.setAttribute("ulist",ulist);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
