import crm.settings.domain.User;
import crm.utils.DateTimeUtil;
import crm.utils.SqlSessionUtil;
import crm.workbench.dao.activityDao;

import java.util.*;

public class test0 {
    public static void main(String[] args) {
        ResourceBundle resourceBundle=ResourceBundle.getBundle("possibility");
        Enumeration<String> keys=resourceBundle.getKeys();
        Map<String,String> map=new HashMap<>();

        while (keys.hasMoreElements()){
            String key=keys.nextElement();
            map.put(key,resourceBundle.getString(key));

        }

    }
}
