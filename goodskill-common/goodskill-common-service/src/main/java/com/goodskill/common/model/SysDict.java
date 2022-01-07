package com.goodskill.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统字典表
 * </p>
 *
 * @author heng
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict")
public class SysDict extends BaseColEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "上级id")
    private String parentId;

    @ApiModelProperty(value = "编码")
    private String dictCode;

    @ApiModelProperty(value = "key")
    private String dictKey;

    @ApiModelProperty(value = "值")
    private String dictValue;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "菜单排序")
    private Long sortNo;

}
