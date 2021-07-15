package crm.web.filter;

import crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class loginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        User user=(User) request.getSession(false).getAttribute("user");
        String path=request.getServletPath();
        if("/login.jsp".equals(path) | "/settings/user/login.do".equals(path)){
            filterChain.doFilter(request,response);
        }
        if(user != null){
            request.getRequestDispatcher("workbench/index.jsp").forward(request,response);
        }else {
            response.sendRedirect(request.getContextPath()+"/login.jsp");
        }
    }
}
