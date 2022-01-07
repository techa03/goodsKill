package com.goodskill.common.service.dubbo;

import cn.hutool.core.bean.BeanUtil;
import com.goodskill.common.api.dto.DictSimpleDTO;
import com.goodskill.common.api.dubbo.GlobalDictionaryService;
import com.goodskill.common.model.SysDict;
import com.goodskill.common.service.SysDictService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

@DubboService
public class GlobalDictionaryServiceImpl implements GlobalDictionaryService {

    @Autowired
    private SysDictService sysDictService;

    @Override
    public List<DictSimpleDTO> getSimpleDictByCode(String dictCode) {
        List<SysDict> sysDicts = sysDictService.getDictByCode(dictCode);
        return CollectionUtils.isEmpty(sysDicts) ? null : BeanUtil.copyToList(sysDicts, DictSimpleDTO.class);
    }

    @Override
    public DictSimpleDTO getSimpleDictByDictCodeAndDictKey(String dictCode, String dictKey) {
        SysDict dict = sysDictService.getDictByDictCodeAndDictKey(dictCode, dictKey);
        return dict == null ? null : BeanUtil.copyProperties(dict, DictSimpleDTO.class);
    }

    @Override
    public DictSimpleDTO getSimpleDictByDictCodeAndDictValue(String dictCode, String dictValue) {
        SysDict dict = sysDictService.getDictByDictCodeAndDictValue(dictCode, dictValue);
        return dict == null ? null : BeanUtil.copyProperties(dict, DictSimpleDTO.class);
    }
}
