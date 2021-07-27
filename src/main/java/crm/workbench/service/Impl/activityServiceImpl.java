package crm.workbench.service.Impl;

import crm.settings.dao.UserDao;
import crm.settings.domain.User;
import crm.utils.SqlSessionUtil;
import crm.vo.queryVO;
import crm.workbench.dao.activityDao;
import crm.workbench.dao.remarkDao.activityRemarkDao;
import crm.workbench.domain.Activity;
import crm.workbench.domain.ActivityRemark;
import crm.workbench.service.activityService;

import java.util.List;
import java.util.Map;

public class activityServiceImpl implements activityService {
    activityDao dao=SqlSessionUtil.getSqlSession().getMapper(activityDao.class);
    activityRemarkDao remarkDao=SqlSessionUtil.getSqlSession().getMapper(activityRemarkDao.class);
    @Override
    public List<User> selectUserList() {

        List<User> ulist=dao.selectUserList();
        return ulist;
    }

    @Override
    public int saveActivity(Activity activity) {
        int count=dao.saveActivity(activity);
        return count;
    }

    @Override
    public queryVO<Activity> searchActivityList(Map<String, Object> map) {
        int count=dao.searchTotal(map);
        List<Activity> list=dao.searchActivityList(map);
        queryVO<Activity> vo=new queryVO<>();
        vo.setTotalsize(count);
        vo.setDatalist(list);
        return vo;
    }

    @Override
    public int deleteActivityandRemark(String[] ids) {
        int c=dao.deleteActivity(ids);
        int a=remarkDao.deleteRemark(ids);
        return c+a;
    }

    @Override
    public Activity editActivity(String id) {
        Activity activity=dao.editActivity(id);
        return activity;
    }

    @Override
    public int updateActivity(Activity activity) {
        int flag=dao.updateActivity(activity);
        return flag;
    }

    @Override
    public Activity activityDetail(String id) {
        Activity activity=dao.editActivity(id);
        return activity;
    }

    @Override
    public List<ActivityRemark> remarkList(String aid) {
        List<ActivityRemark> remarks=remarkDao.remarkList(aid);
        return remarks;
    }

    @Override
    public int deleteRemark(String id) {
        int count=remarkDao.deleteRemarkById(id);
        return count;
    }

    @Override
    public int saveRemark(ActivityRemark ar) {
        int a=remarkDao.saveRemark(ar);
        return a;
    }

    @Override
    public ActivityRemark reShowRemark(String id) {
        ActivityRemark activityRemark=remarkDao.reShowRemark(id);
        return activityRemark;
    }

    @Override
    public List<Activity> getActivityByClueId(String clueId) {
        List<Activity> alist=dao.getActivityByClueId(clueId);
        return alist;
    }

    @Override
    public List<Activity> searchUnbundActivity(Map<String, String> map) {
        List<Activity> alist=dao.searchUnbundActivity(map);
        return alist;
    }

    @Override
    public List<Activity> searchActivity(String name) {
        List<Activity> alist=dao.searchActivity(name);
        return alist;
    }


}
