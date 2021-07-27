package crm.workbench.dao;

import crm.settings.domain.User;
import crm.workbench.domain.Tran;

import java.util.List;

public interface TranDao {

    int save(Tran tran);

    List<User> getUlist();


    Tran getTranDetail(String tranId);

    void update(Tran t);
}
