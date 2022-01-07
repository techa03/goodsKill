package com.goodskill.common.service.dubbo;

import cn.hutool.core.bean.BeanUtil;
import com.goodskill.common.api.dto.DictSimpleDTO;
import com.goodskill.common.api.dubbo.DictionaryService;
import com.goodskill.common.model.SysDictBiz;
import com.goodskill.common.service.SysDictBizService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author heng
 * @since 2021/12/7
 */
@DubboService
public class DictionaryServiceImpl implements DictionaryService {
    @Autowired
    private SysDictBizService sysDictBizService;

    @Override
    public List<DictSimpleDTO> getSimpleDictByCode(String dictCode, String systemCode) {
        List<SysDictBiz> sysDictBizs = sysDictBizService.getDictByCode(dictCode, systemCode);
        return CollectionUtils.isEmpty(sysDictBizs) ? null : BeanUtil.copyToList(sysDictBizs, DictSimpleDTO.class);
    }

    @Override
    public DictSimpleDTO getSimpleDictByDictCodeAndDictKey(String dictCode, String dictKey, String systemCode) {
        SysDictBiz dict = sysDictBizService.getDictByDictCodeAndDictKey(dictCode, dictKey, systemCode);
        return dict == null ? null : BeanUtil.copyProperties(dict, DictSimpleDTO.class);
    }

    @Override
    public DictSimpleDTO getSimpleDictByDictCodeAndDictValue(String dictCode, String dictValue, String systemCode) {
        SysDictBiz dict = sysDictBizService.getDictByDictCodeAndDictValue(dictCode, dictValue, systemCode);
        return dict == null ? null : BeanUtil.copyProperties(dict, DictSimpleDTO.class);
    }

}
