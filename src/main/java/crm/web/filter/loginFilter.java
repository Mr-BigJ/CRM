package crm.web.filter;

import crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class loginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("进入登录过滤");
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        String path=request.getServletPath();
        //不需要拦截的请求
        if("/login.jsp".equals(path) | "/settings/user/login.do".equals(path)){
            filterChain.doFilter(request,response);

        }else {
            User user=(User) request.getSession(false).getAttribute("user");
            if(user != null){
                filterChain.doFilter(request,response);
            }else {
                //request.getContextPath()拿到项目名/crm
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }
    }
}
