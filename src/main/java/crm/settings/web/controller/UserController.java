package crm.settings.web.controller;


import crm.settings.domain.User;
import crm.settings.service.UserService;
import crm.settings.service.impl.UserServiceImpl;
import crm.utils.MD5Util;
import crm.utils.PrintJson;
import crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入控制器");
        String path=request.getServletPath();
        if("/settings/user/login.do".equals(path)){
            login(request,response);
        }else {

        }
    }



    private void login(HttpServletRequest request, HttpServletResponse response){
        String loginid=request.getParameter("loginId");
        String loginpwd=request.getParameter("loginpwd");
        loginpwd= MD5Util.getMD5(loginpwd);
        String ip=request.getRemoteAddr();

        //未来业务层开发，统一使用代理类形态的接口对象
        UserService service=(UserService) ServiceFactory.getService(new UserServiceImpl());
        try{
            User user=service.login(loginid,loginpwd,ip);
            //当user为空时，手动抛异常
            request.getSession().setAttribute("user",user);
            //如果程序执行到这，说明user不为空
            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
            e.printStackTrace();
            String msg=e.getMessage();
            Map<String,Object> map=new HashMap<>();
            map.put("msg",msg);
            map.put("success",false);
            PrintJson.printJsonObj(response,map);
        }

    }
}
