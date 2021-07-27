package crm.workbench.service;

import crm.settings.domain.User;
import crm.vo.queryVO;
import crm.workbench.domain.Activity;
import crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface activityService {
    List<User> selectUserList();

    int saveActivity(Activity activity);

    queryVO<Activity> searchActivityList(Map<String, Object> map);


    int deleteActivityandRemark(String[] ids);

    Activity editActivity(String id);

    int updateActivity(Activity activity);

    Activity activityDetail(String id);

    List<ActivityRemark> remarkList(String aid);

    int deleteRemark(String id);

    int saveRemark(ActivityRemark ar);

    ActivityRemark reShowRemark(String id);

    List<Activity> getActivityByClueId(String clueId);

    List<Activity> searchUnbundActivity(Map<String, String> map);

    List<Activity> searchActivity(String name);
}
