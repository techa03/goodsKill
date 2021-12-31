package com.goodskill.common.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 业务字典表，只带code value
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DictSimpleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 菜单排序
     */
    private Long sortNo;

}
