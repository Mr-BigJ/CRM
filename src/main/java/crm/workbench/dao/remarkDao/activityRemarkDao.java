package crm.workbench.dao.remarkDao;

import crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface activityRemarkDao {
    int deleteRemark(String[] ids);

    List<ActivityRemark> remarkList(String aid);

    int deleteRemarkById(String id);

    int saveRemark(ActivityRemark ar);

    ActivityRemark reShowRemark(String id);
}
