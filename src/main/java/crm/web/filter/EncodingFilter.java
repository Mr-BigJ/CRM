package crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("进入编码过滤器");
        //为请求对象设置编码
        servletRequest.setCharacterEncoding("utf-8");
        //为响应对象设置编码
        servletResponse.setCharacterEncoding("utf-8");
        //通行
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
