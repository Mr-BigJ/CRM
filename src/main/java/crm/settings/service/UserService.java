package crm.settings.service;


import crm.exception.LoginException;
import crm.settings.domain.User;

public interface UserService {
    User login(String loginid, String loginpwd, String ip) throws LoginException;
}
