package crm.listener;

import crm.settings.domain.dic_type;
import crm.settings.domain.dic_value;
import crm.settings.service.DicService;
import crm.settings.service.impl.DicServiceImpl;
import crm.utils.ServiceFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysAppListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("进入上下文域对象监听");
        ServletContext application=sce.getServletContext();
        //通过业务层取数据字典
        DicService dicService= (DicService) ServiceFactory.getService(new DicServiceImpl());
        /*
        * 应该管业务层要7个list，每个list存放的是每个类型的值
        * 可以把7个list打包成一个map
        * */
        Map<String, List<dic_value>> map=dicService.getAll();
        //将map解析成上下文域对象中保存的键值对
        Set<String> set=map.keySet();
        for (String key:set) {
            application.setAttribute(key,map.get(key));

        }
        //创建可能性
        ResourceBundle resourceBundle=ResourceBundle.getBundle("possibility");
        Enumeration<String> keys=resourceBundle.getKeys();
        Map<String,String> possibility=new HashMap<>();

        while (keys.hasMoreElements()){
            String key=keys.nextElement();
            possibility.put(key,resourceBundle.getString(key));

        }
        application.setAttribute("possibility",possibility);

    }
}
