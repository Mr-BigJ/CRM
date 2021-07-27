package crm.workbench.dao;


import crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    List<ClueActivityRelation> getListByculeId(String clueId);

    int delete(ClueActivityRelation clueActivityRelation);
}
