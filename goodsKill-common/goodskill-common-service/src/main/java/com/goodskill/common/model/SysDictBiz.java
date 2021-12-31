package com.goodskill.common.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 业务字典表
 * </p>
 *
 * @author heng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_biz")
public class SysDictBiz extends SysDict {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "所属系统")
    @NotBlank
    private String sysCode;


}
