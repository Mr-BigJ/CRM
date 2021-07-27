package crm.workbench.service.Impl;

import crm.utils.DateTimeUtil;
import crm.utils.SqlSessionUtil;
import crm.utils.UUIDUtil;
import crm.workbench.dao.*;
import crm.workbench.domain.*;
import crm.workbench.service.clueService;

import java.util.List;

public class clueServiceImpl implements clueService {
    ClueDao clueDao=SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    ClueRemarkDao clueRemarkDao=SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    ClueActivityRelationDao clueActivityRelationDao=SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);

    ContactsDao contactsDao=SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    ContactsRemarkDao contactsRemarkDao=SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    ContactsActivityRelationDao contactsActivityRelationDao=SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    CustomerDao customerDao=SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    CustomerRemarkDao customerRemarkDao=SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    TranDao tranDao=SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    TranHistoryDao tranHistoryDao=SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean saveClue(Clue clue) {
        boolean f=clueDao.saveClue(clue);
        return f;
    }

    @Override
    public Clue clueDetail(String id) {
        Clue clue=clueDao.clueDetail(id);
        return clue;
    }

    @Override
    public boolean unbund(String id) {
        boolean f=clueDao.unbund(id);
        return f;
    }

    @Override
    public boolean relateA(String cid, String[] aids) {
        boolean f=false;
        for (String aid:aids) {
            ClueActivityRelation car=new ClueActivityRelation();
            car.setActivityId(aid);
            car.setClueId(cid);
            car.setId(UUIDUtil.getUUID());
            int count=clueDao.relateA(car);
            if(count==1){
                f=true;
            }
        }

        return f;
    }

    @Override
    public boolean switchC(String clueId, Tran tran, String createBy) {
        boolean f=true;
        String createTime= DateTimeUtil.getSysTime();
        //1.通过线索id查单条
        Clue clue=clueDao.getById(clueId);
        //2.通过线索对象提取客户信息，当该客户不存在时，新建客户，（根据公司名称精确匹配）
        String company=clue.getCompany();
        Customer c1=customerDao.getCustomerByName(company);
        //如果用户为空，新建用户信息
        if(c1==null){
            c1=new Customer();
            c1.setAddress(clue.getAddress());
            c1.setCreateBy(createBy);
            c1.setCreateTime(createTime);
            c1.setId(UUIDUtil.getUUID());
            c1.setDescription(clue.getDescription());
            c1.setName(company);
            c1.setWebsite(clue.getWebsite());
            c1.setPhone(clue.getPhone());
            c1.setOwner(clue.getOwner());
            c1.setNextContactTime(clue.getNextContactTime());
            c1.setContactSummary(clue.getContactSummary());
            //添加客户
            int count1=customerDao.save(c1);
            if(count1!=1){
                f=false;
            }
        }
        //3.通过线索对象提取联系人信息，保存联系人
        Contacts contacts=new Contacts();
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setId(UUIDUtil.getUUID());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setCustomerId(c1.getId());
        contacts.setDescription(clue.getDescription());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setAppellation(clue.getAppellation());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAddress(clue.getAddress());
        //添加联系人
        int count2=contactsDao.save(contacts);
        if(count2!=1){
            f=false;
        }
        //经过第三步处理后，联系人的信息我们已经有了，在使用其他表的时候需要联系人id直接contacts.getId

        //4.线索备注转换到客户备注和联系人备注
        //查询出与该线索关联的备注信息列表
        List<ClueRemark> clueRemarkList=clueRemarkDao.getListByClueId(clueId);
        for (ClueRemark clueRemark:clueRemarkList) {
            //取出备注信息，主要转换到客户备注和联系人备注
            String noteContent = clueRemark.getNoteContent();
            //创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setNoteContent(noteContent);
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(c1.getId());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            int count3 = customerRemarkDao.save(customerRemark);
            if (count3 != 1) {
                f = false;
            }

            //创建联系人备注对象，添加联系人备注
            ContactsRemark cr = new ContactsRemark();
            cr.setNoteContent(noteContent);
            cr.setId(UUIDUtil.getUUID());
            cr.setEditFlag("0");
            cr.setContactsId(contacts.getId());
            cr.setCreateTime(createTime);
            cr.setCreateBy(createBy);
            int count4 = contactsRemarkDao.save(cr);
            if (count4 != 1) {
                f = false;
            }
        }

        //5.线索和市场活动的关系转换到联系人和市场活动的关系
        //查询出与该条线索关联的市场活动，查询与市场活动的关联关系列表
        List<ClueActivityRelation> clueActivityRelations=clueActivityRelationDao.getListByculeId(clueId);
        for(ClueActivityRelation c:clueActivityRelations){
            //从每一条遍历的记录中取出关联的市场活动id
            String activityId=c.getActivityId();
            //创建联系人与市场活动的关联关系对象，让第三步生成的联系人与市场活动做关联
            ContactsActivityRelation contactsActivityRelation=new ContactsActivityRelation();
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            //添加联系人与市场活动的关联关系
            int count5=contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5!=1){
                f=false;
            }
        }

        //6.如果有创建交易的需求，创建一条交易
        if(tran != null){
            //在controller中已经完成一些信息的封装，这是继续完善
            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(c1.getId());
            tran.setContactSummary(clue.getContactSummary());
            tran.setContactsId(contacts.getId());
            //添加交易
            int count6=tranDao.save(tran);
            if(count6 != 1){
                f=false;
            }

            //7.创建交易历史，有交易才创建，交易跟交易历史是一对多的关系
            TranHistory th=new TranHistory();
            th.setTranId(tran.getId());
            th.setStage(tran.getStage());
            th.setMoney(tran.getMoney());
            th.setId(UUIDUtil.getUUID());
            th.setExpectedDate(tran.getExpectedDate());
            th.setCreateTime(createTime);
            th.setCreateBy(createBy);
            int count7=tranHistoryDao.save(th);
            if(count7!=1){
                f=false;
            }
        }

        //8.删除线索备注
        for (ClueRemark clueRemark:clueRemarkList) {
            int count8=clueRemarkDao.delete(clueRemark);
            if(count8!=1){
                f=false;
            }
        }

        //9.删除线索和市场活动的关系
        for(ClueActivityRelation clueActivityRelation:clueActivityRelations){
            int count9=clueActivityRelationDao.delete(clueActivityRelation);
            if(count9!=1){
                f=false;
            }
        }

        //10.删除线索
        int count10=clueDao.delete(clueId);
        if(count10!=1){
            f=false;
        }
        return f;
    }


}
