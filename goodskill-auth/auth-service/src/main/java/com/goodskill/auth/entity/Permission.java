package com.goodskill.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.goodskill.common.core.entity.BaseColEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author heng
 * @since 2019-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Permission extends BaseColEntity implements Serializable {

    @Serial
    private static final long serialVersionUID=1L;

    @TableId(value = "permission_id", type = IdType.AUTO)
    private Integer permissionId;

    private String permissionName;

    private String permissionMenu;

    private Integer parentPermissionId;

    private String isDir;

    private Integer orderNo;

    /**
     * 权限标识
     */
    private String permissionCode;


}
