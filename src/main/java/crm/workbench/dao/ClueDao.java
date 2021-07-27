package crm.workbench.dao;


import crm.workbench.domain.Clue;
import crm.workbench.domain.ClueActivityRelation;

public interface ClueDao {


    boolean saveClue(Clue clue);

    Clue clueDetail(String id);

    boolean unbund(String id);

    int relateA(ClueActivityRelation car);

    Clue getById(String clueId);

    int delete(String clueId);
}
