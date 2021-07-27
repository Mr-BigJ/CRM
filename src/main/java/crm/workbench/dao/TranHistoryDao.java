package crm.workbench.dao;

import crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    int save(TranHistory th);

    List<TranHistory> searchHistory(String tranId);
}
