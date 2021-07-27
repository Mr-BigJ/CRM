package crm.workbench.service;

import crm.workbench.domain.Clue;
import crm.workbench.domain.Tran;

public interface clueService {
    boolean saveClue(Clue clue);

    Clue clueDetail(String id);

    boolean unbund(String id);

    boolean relateA(String cid, String[] aids);


    boolean switchC(String clueId, Tran tran, String createBy);
}
