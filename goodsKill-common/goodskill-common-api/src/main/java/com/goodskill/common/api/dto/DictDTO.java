package com.goodskill.common.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 业务字典表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private String id;

    /**
     * 上级id
     */
    private String parentId;

    /**
     * 编码
     */
    private String dictCode;

    /**
     * key
     */
    private String dictKey;

    /**
     * 值
     */
    private String dictValue;

    /**
     * 所属系统
     */
    private String systemCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 菜单排序
     */
    private Long sortNo;

}
