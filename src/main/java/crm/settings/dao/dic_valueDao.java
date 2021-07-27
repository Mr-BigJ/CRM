package crm.settings.dao;

import crm.settings.domain.dic_type;
import crm.settings.domain.dic_value;

import java.util.List;

public interface dic_valueDao {
    List<dic_value> getValueByCode(String code);
}
