package crm.settings.service.impl;

import crm.exception.LoginException;
import crm.settings.dao.UserDao;
import crm.settings.domain.User;
import crm.settings.service.UserService;
import crm.utils.DateTimeUtil;
import crm.utils.SqlSessionUtil;
import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl implements UserService {
    UserDao dao= SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginid, String loginpwd, String ip) throws LoginException {
        Map<String,String> map=new HashMap<>();
        map.put("loginid",loginid);
        map.put("loginpwd",loginpwd);
        User user=dao.login(map);
        if(user != null){
            //验证失效时间
            String expiretime=user.getExpireTime();
            String state=user.getLockState();
            if(expiretime.compareTo(DateTimeUtil.getSysTime()) < 0){
                throw new LoginException("账号已经失效");

            }else if("1".equals(state)){
                //验证锁定状态
                throw new LoginException("账号已锁定");
            }
        }else {
            throw new LoginException("账号密码错误");
        }
        return user;
    }
}
