package com.goodskill.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.goodskill.common.mapper.SysDictBizMapper;
import com.goodskill.common.model.SysDictBiz;
import com.goodskill.common.service.SysDictBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * @author 27568
 * @date 2021/4/8 4:24 下午
 */
@Service
@CacheConfig(cacheNames = "common:dict:biz")
@Slf4j
public class SysDictBizServiceImpl extends ServiceImpl<SysDictBizMapper, SysDictBiz> implements SysDictBizService {

    @Autowired
    private SysDictBizService sysDictBizService;

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#sysDictBiz.dictCode+'|'+#sysDictBiz.sysCode",
                    condition = "#sysDictBiz.dictCode!=null&&#sysDictBiz.sysCode!=null"),
            @CacheEvict(key = "#sysDictBiz.dictCode+'|'+#sysDictBiz.dictKey+'|'+#sysDictBiz.sysCode",
                    condition = "#sysDictBiz.dictCode!=null&&#sysDictBiz.dictKey!=null&&#sysDictBiz.sysCode!=null"),
            @CacheEvict(key = "#sysDictBiz.dictCode+'|'+#sysDictBiz.dictValue+'|'+#sysDictBiz.sysCode",
                    condition = "#sysDictBiz.dictCode!=null&&#sysDictBiz.dictValue!=null&&#sysDictBiz.sysCode!=null")
    })
    public boolean save(SysDictBiz sysDictBiz) {
        sysDictBiz.setId(IdUtil.simpleUUID());
        return SqlHelper.retBool(baseMapper.insert(sysDictBiz));
    }

    @Cacheable(key = "#dictCode+'|'+#systemCode", sync = true,
            condition = "#dictCode!=null&&#systemCode!=null")
    @Override
    public List<SysDictBiz> getDictByCode(String dictCode, String systemCode) {
        log.debug("#getDictByCode 未命中缓存！本次使用数据库查询");
        return baseMapper.selectList(new LambdaQueryWrapper<SysDictBiz>()
                .eq(StringUtils.hasText(dictCode), SysDictBiz::getDictCode, dictCode)
                .eq(StringUtils.hasText(systemCode), SysDictBiz::getSysCode, systemCode));
    }

    @Override
    public List<SysDictBiz> queryDictByParentId(String parentId, String systemCode) {
        return baseMapper.selectList(new QueryWrapper<SysDictBiz>().eq("parent_id", parentId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(SysDictBiz sysDictBiz) {
        // 校验是否重复
        if (this.count(new LambdaQueryWrapper<SysDictBiz>()
                .eq(SysDictBiz::getDictKey, sysDictBiz.getDictKey())
                .eq(SysDictBiz::getDictValue, sysDictBiz.getDictValue())
                .eq(SysDictBiz::getSysCode, sysDictBiz.getSysCode())) > 0) {
            log.warn("待更新字典项有重复，请确认数据: {}", sysDictBiz);
            return false;
        }
        // 刷新缓存
        SysDictBiz dictBiz = baseMapper.selectById(sysDictBiz.getId());
        if (dictBiz == null) {
            throw new RuntimeException("不存在的字典！");
        }
        sysDictBizService.evictCacheByDictCodeAndDictKey(dictBiz.getDictCode(), dictBiz.getDictKey(), dictBiz.getSysCode());
        sysDictBizService.evictCacheByDictCodeAndDictValue(dictBiz.getDictCode(), dictBiz.getDictValue(), dictBiz.getSysCode());
        int i = baseMapper.updateById(BeanUtil.copyProperties(sysDictBiz, SysDictBiz.class));
        return SqlHelper.retBool(i);
    }

    @Override
    public boolean removeByIds(Collection idList) {
        List<SysDictBiz> sysDictBizs = baseMapper.selectList(new LambdaQueryWrapper<SysDictBiz>().in(SysDictBiz::getId, idList)
                .select(SysDictBiz::getDictCode, SysDictBiz::getSysCode,
                        SysDictBiz::getParentId, SysDictBiz::getDictKey, SysDictBiz::getDictValue));
        int result = baseMapper.deleteBatchIds(idList);
        // 使对应的缓存失效
        sysDictBizs.forEach(it -> {
            sysDictBizService.evictCacheByDictCode(it.getDictCode(), it.getSysCode());
            sysDictBizService.evictCacheByDictCodeAndDictKey(it.getDictCode(), it.getDictKey(), it.getSysCode());
            sysDictBizService.evictCacheByDictCodeAndDictValue(it.getDictCode(), it.getDictValue(), it.getSysCode());
        });
        return SqlHelper.retBool(result);
    }

    @Override
    @CacheEvict(key = "#dictCode+'|'+#systemCode")
    public boolean evictCacheByDictCode(String dictCode, String systemCode) {
        return true;
    }

    @Override
    @CacheEvict(key = "#dictCode+'|'+#dictKey+'|'+#systemCode")
    public boolean evictCacheByDictCodeAndDictKey(String dictCode, String dictKey, String systemCode) {
        return true;
    }

    @Override
    @CacheEvict(key = "#dictCode+'|'+#dictValue+'|'+#systemCode")
    public boolean evictCacheByDictCodeAndDictValue(String dictCode, String dictValue, String systemCode) {
        return true;
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean evictAll() {
        return true;
    }

    @Override
    @Cacheable(key = "#dictCode+'|'+#dictKey+'|'+#systemCode", sync = true)
    public SysDictBiz getDictByDictCodeAndDictKey(String dictCode, String dictKey, String systemCode) {
        log.debug("#getDictByDictCodeAndDictKey 未命中缓存！本次使用数据库查询");
        return baseMapper.selectOne(new LambdaQueryWrapper<SysDictBiz>()
                .eq(SysDictBiz::getSysCode, systemCode)
                .eq(SysDictBiz::getDictCode, dictCode)
                .eq(SysDictBiz::getDictKey, dictKey));
    }

    @Override
    @Cacheable(key = "#dictCode+'|'+#dictValue+'|'+#systemCode", sync = true)
    public SysDictBiz getDictByDictCodeAndDictValue(String dictCode, String dictValue, String systemCode) {
        log.debug("#getDictByDictCodeAndDictValue 未命中缓存！本次使用数据库查询");
        return baseMapper.selectOne(new LambdaQueryWrapper<SysDictBiz>()
                .eq(SysDictBiz::getSysCode, systemCode)
                .eq(SysDictBiz::getDictCode, dictCode)
                .eq(SysDictBiz::getDictValue, dictValue));
    }

}
