package crm.workbench.service;

import crm.settings.domain.User;
import crm.workbench.domain.Tran;
import crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranService {
    List<User> save();


    List<String> getCustomerName(String name);

    boolean saveTran(Tran tran, String customerName);

    Tran getTranDetail(String tranId);

    List<TranHistory> searchHistory(String tranId);

    Tran changeStage(Tran t);
}
