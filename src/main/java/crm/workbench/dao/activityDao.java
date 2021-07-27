package crm.workbench.dao;

import crm.settings.domain.User;
import crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface activityDao {
    List<User> selectUserList();

    int saveActivity(Activity activity);

    int searchTotal(Map<String, Object> map);

    List<Activity> searchActivityList(Map<String, Object> map);

    int deleteActivity(String[] ids);

    Activity editActivity(String id);

    int updateActivity(Activity activity);

    List<Activity> getActivityByClueId(String clueId);

    List<Activity> searchUnbundActivity(Map<String, String> map);

    List<Activity> searchActivity(String name);
}
