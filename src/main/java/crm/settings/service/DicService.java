package crm.settings.service;

import crm.settings.domain.dic_type;
import crm.settings.domain.dic_value;

import java.util.List;
import java.util.Map;

public interface DicService {
    Map<String, List<dic_value>> getAll();
}
