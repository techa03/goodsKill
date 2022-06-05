package com.goodskill.common.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.goodskill.common.mapper.SysDictMapper;
import com.goodskill.common.model.SysDict;
import com.goodskill.common.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 系统字典管理
 * 注：先不使用缓存优化
 *
 * @date 2021/4/8 4:24 下午
 */
@Service
@CacheConfig(cacheNames = "common:dict:sys")
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    @Autowired
    private SysDictService sysDictService;

    @Override
    public boolean save(SysDict sysDict) {
        sysDict.setId(IdUtil.simpleUUID());
        return SqlHelper.retBool(baseMapper.insert(sysDict));
    }

    @Override
    public List<SysDict> getDictByCode(String dictCode) {
        return baseMapper.selectList(new LambdaQueryWrapper<SysDict>()
                .eq(StringUtils.hasText(dictCode), SysDict::getDictCode, dictCode));
    }

    @Override
    public boolean evictCacheByDictCode(String dictCode) {
        return false;
    }

    @Override
    public boolean evictCacheByDictCodeAndDictKey(String dictCode, String dictKey) {
        return false;
    }

    @Override
    public boolean evictCacheByDictCodeAndDictValue(String dictCode, String dictValue) {
        return false;
    }

    @Override
    public boolean evictAll() {
        return false;
    }

    @Override
    public List<SysDict> queryDictByParentId(String parentId) {
        return baseMapper.selectList(new QueryWrapper<SysDict>().eq("parent_id", parentId));
    }


    @Override
    public boolean removeByIds(Collection idList) {
        int result = baseMapper.deleteBatchIds(idList);
        return SqlHelper.retBool(result);
    }

    @Override
    public SysDict getDictByDictCodeAndDictKey(String dictCode, String dictKey) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictCode, dictCode)
                .eq(SysDict::getDictKey, dictKey));
    }

    @Override
    public SysDict getDictByDictCodeAndDictValue(String dictCode, String dictValue) {
        return baseMapper.selectOne(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictCode, dictCode)
                .eq(SysDict::getDictValue, dictValue));
    }
}
