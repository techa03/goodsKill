package com.goodskill.common.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.goodskill.common.model.SysDict;

import java.util.List;

/**
 * 系统字典管理service
 *
 * @author 27568
 * @date 2021/11/04
 */
public interface SysDictService extends IService<SysDict> {

    /**
     * 根据父字典id查找字典
     *
     * @param parentId   所属字典id
     * @return
     */
    List<SysDict> queryDictByParentId(String parentId);

    /**
     * 获取业务字典列表
     *
     * @param dictCode   字典code，例如：字典"sex:1-男"中，字典code对应"sex"
     * @return 字典列表
     */
    List<SysDict> getDictByCode(String dictCode);

    /**
     * 根据系统编码以及字典code清除缓存
     *
     * @param dictCode   字典code
     * @return 是否清除成功
     */
    boolean evictCacheByDictCode(String dictCode);

    /**
     * 根据系统编码以及字典code清除缓存
     *
     * @param dictCode   字典code
     * @param dictKey    字典key
     * @return 是否清除成功
     */
    boolean evictCacheByDictCodeAndDictKey(String dictCode, String dictKey);

    /**
     * 根据系统编码以及字典code清除缓存
     *
     * @param dictCode   字典code
     * @param dictValue  字典value
     * @return 是否清除成功
     */
    boolean evictCacheByDictCodeAndDictValue(String dictCode, String dictValue);

    /**
     * 清除所有缓存
     *
     * @return 是否清除成功
     */
    boolean evictAll();

    /**
     * 获取单个业务字典
     *
     * @param dictCode   字典code，例如：字典"sex:1-男"中，字典code对应"sex"
     * @param dictKey    字典类型key，例如：字典"1-男"中，字典key对应"1"
     * @return 字典列表
     */
    SysDict getDictByDictCodeAndDictKey(String dictCode, String dictKey);

    /**
     * 获取单个业务字典，
     *
     * @param dictCode   字典code，例如：字典"sex:1-男"中，字典code对应"sex"
     * @param dictValue  字典值，例如：字典"1-男"中，字典值对应"男"
     * @return 字典列表
     */
    SysDict getDictByDictCodeAndDictValue(String dictCode, String dictValue);
}
