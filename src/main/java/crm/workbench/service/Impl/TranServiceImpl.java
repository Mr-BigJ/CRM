package crm.workbench.service.Impl;

import crm.settings.domain.User;
import crm.utils.SqlSessionUtil;
import crm.utils.UUIDUtil;
import crm.workbench.dao.CustomerDao;
import crm.workbench.dao.TranDao;
import crm.workbench.dao.TranHistoryDao;
import crm.workbench.domain.Customer;
import crm.workbench.domain.Tran;
import crm.workbench.domain.TranHistory;
import crm.workbench.service.TranService;

import java.util.List;

public class TranServiceImpl implements TranService {
    TranDao tranDao= SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    TranHistoryDao tranHistoryDao=SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    CustomerDao customerDao=SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    @Override
    public List<User> save() {
        List<User> ulist=tranDao.getUlist();
        return ulist;
    }

    @Override
    public List<String> getCustomerName(String name) {
        List<String> list=customerDao.getCustomerName(name);

        return list;
    }

    @Override
    public boolean saveTran(Tran tran, String customerName) {
        boolean f=true;
        //1.先查询是否存在该客户，若无则创建，再取id，有直接取id
        Customer customer=customerDao.getCustomerByName(customerName);
        if(customer==null){
            customer=new Customer();
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(tran.getCreateTime());
            customer.setId(UUIDUtil.getUUID());
            customer.setDescription(tran.getDescription());
            customer.setName(customerName);
            customer.setOwner(tran.getOwner());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setContactSummary(tran.getContactSummary());
            //添加一条客户
            int count=customerDao.save(customer);
            if(count!=1){
                f=false;
            }
        }
        //2.取得customerId，封装到tran，执行添加,同时还要添加交易历史
        String customerId=customer.getId();
        tran.setCustomerId(customerId);
        int c=tranDao.save(tran);
        if(c!=1){
            f=false;
        }
        TranHistory th=new TranHistory();
        th.setTranId(tran.getId());
        th.setStage(tran.getStage());
        th.setMoney(tran.getMoney());
        th.setId(UUIDUtil.getUUID());
        th.setExpectedDate(tran.getExpectedDate());
        th.setCreateTime(tran.getCreateTime());
        th.setCreateBy(tran.getCreateBy());
        int co=tranHistoryDao.save(th);
        if(co!=1){
            f=false;
        }
        return f;
    }

    @Override
    public Tran getTranDetail(String tranId) {
        return tranDao.getTranDetail(tranId);
    }

    @Override
    public List<TranHistory> searchHistory(String tranId) {
        List<TranHistory> hlist=tranHistoryDao.searchHistory(tranId);
        return hlist;
    }

    @Override
    public Tran changeStage(Tran t) {
        //1.更新tran
        tranDao.update(t);
        //2.创建交易历史
        TranHistory th=new TranHistory();
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setId(UUIDUtil.getUUID());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(t.getEditTime());
        tranHistoryDao.save(th);
        //3.查询更新后的tran返回
        Tran tr=tranDao.getTranDetail(t.getId());
        return tr;
    }


}
