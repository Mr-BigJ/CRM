package crm.settings.service.impl;

import crm.settings.dao.dic_typeDao;
import crm.settings.dao.dic_valueDao;
import crm.settings.domain.dic_type;
import crm.settings.domain.dic_value;
import crm.settings.service.DicService;
import crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    dic_valueDao valueDao=SqlSessionUtil.getSqlSession().getMapper(dic_valueDao.class);
    dic_typeDao typeDao=SqlSessionUtil.getSqlSession().getMapper(dic_typeDao.class);
    @Override
    public Map<String, List<dic_value>> getAll() {
        Map<String,List<dic_value>> map=new HashMap<>();
        List<String> typelist=typeDao.getAllType();
        for (String code:typelist) {
            //再根据查询的类型循环查询所有类型的值保存到list中
            List<dic_value> list=valueDao.getValueByCode(code);
            //对于map，key不能重复，value可以重复
            map.put(code+"list",list);
        }
        return map;
    }
}
