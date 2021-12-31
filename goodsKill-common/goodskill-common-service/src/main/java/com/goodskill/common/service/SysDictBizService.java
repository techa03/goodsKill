package com.goodskill.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goodskill.common.model.SysDictBiz;

import java.util.List;

/**
 * 业务字典管理service
 *
 * @author 27568
 * @date 2021/11/04
 */
public interface SysDictBizService extends IService<SysDictBiz> {

    /**
     * 根据父字典id查找字典
     *
     * @param parentId   所属字典id
     * @param systemCode 字典所属系统编码
     * @return
     */
    List<SysDictBiz> queryDictByParentId(String parentId, String systemCode);

    /**
     * 获取业务字典列表
     *
     * @param dictCode   字典code，例如：字典"sex:1-男"中，字典code对应"sex"
     * @param systemCode 所属系统
     * @return 字典列表
     */
    List<SysDictBiz> getDictByCode(String dictCode, String systemCode);

    /**
     * 根据系统编码以及字典code清除缓存
     *
     * @param dictCode   字典code
     * @param systemCode 系统编码
     * @return 是否清除成功
     */
    boolean evictCacheByDictCode(String dictCode, String systemCode);

    /**
     * 根据系统编码以及字典code清除缓存
     *
     * @param dictCode   字典code
     * @param dictKey    字典key
     * @param systemCode 系统编码
     * @return 是否清除成功
     */
    boolean evictCacheByDictCodeAndDictKey(String dictCode, String dictKey, String systemCode);

    /**
     * 根据系统编码以及字典code清除缓存
     *
     * @param dictCode   字典code
     * @param dictValue  字典value
     * @param systemCode 系统编码
     * @return 是否清除成功
     */
    boolean evictCacheByDictCodeAndDictValue(String dictCode, String dictValue, String systemCode);

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
     * @param systemCode 所属系统
     * @return 字典列表
     */
    SysDictBiz getDictByDictCodeAndDictKey(String dictCode, String dictKey, String systemCode);

    /**
     * 获取单个业务字典，
     *
     * @param dictCode   字典code，例如：字典"sex:1-男"中，字典code对应"sex"
     * @param dictValue  字典值，例如：字典"1-男"中，字典值对应"男"
     * @param systemCode 所属系统
     * @return 字典列表
     */
    SysDictBiz getDictByDictCodeAndDictValue(String dictCode, String dictValue, String systemCode);
}
