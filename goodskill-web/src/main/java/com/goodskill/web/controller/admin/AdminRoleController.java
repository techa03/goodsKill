package com.goodskill.web.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.api.service.*;
import com.goodskill.common.info.R;
import com.goodskill.entity.Permission;
import com.goodskill.entity.Role;
import com.goodskill.entity.RolePermission;
import com.goodskill.web.dto.PermissionDTO;
import com.goodskill.web.dto.RoleDTO;
import com.goodskill.web.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Api(tags = "后台角色权限管理")
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminRoleController {
    @DubboReference
    private RoleService roleService;
    @DubboReference
    private PermissionService permissionService;
    @DubboReference
    private UserAccountService userAccountService;
    @DubboReference
    private UserRoleService userRoleService;
    @DubboReference
    private RolePermissionService rolePermissionService;

    @ApiOperation("分页查询角色")
    @RequestMapping(value = "/role/list", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public R<PageVO<Role>> role(
            @ApiParam("分页当前页码")
            @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
            @ApiParam("分页每页显示数量")
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<Role> page = new Page<>(offset, limit);
        IPage<Role> pageInfo = roleService.page(page);
        PageVO<Role> pageVO = new PageVO<>(pageInfo);
        return R.ok(pageVO);
    }

    @ApiOperation("分页查询角色部分信息")
    @RequestMapping(value = "/role/less", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public R<PageVO<RoleDTO>> roleLess(
            @ApiParam("分页当前页码")
            @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
            @ApiParam("分页每页显示数量")
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<Role> page = new Page<>(offset, limit);
        IPage<Role> pageInfo = roleService.page(page);
        List<Role> list = pageInfo.getRecords();
        List<RoleDTO> result = new ArrayList();
        for (Role role : list) {
            RoleDTO roleDto = new RoleDTO();
            BeanUtils.copyProperties(role, roleDto);
            result.add(roleDto);
        }
        PageVO<RoleDTO> pageVO = new PageVO<>();
        pageVO.setTotal((int) pageInfo.getTotal());
        pageVO.setItems(result);
        return R.ok(pageVO);
    }

    @ApiOperation("新增角色")
    @GetMapping("/role/add")
    public R addRole(Role role) {
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        roleService.save(role);
        return R.ok();
    }

    @GetMapping(value = "/role/delete/{roleId}")
    @ApiOperation("删除角色")
    public R deleteRole(@PathVariable("roleId") int roleId) {
        Role entity = new Role();
        entity.setRoleId(roleId);
        roleService.remove(entity);
        return R.ok();
    }

    @PostMapping(value = "/role/permissions/update/{roleId}")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("更新角色权限")
    public R updateRolePermission(@PathVariable("roleId") int roleId, @RequestBody List<String> permissionIds) {
        rolePermissionService.remove(roleId);
        for (String permissionId : permissionIds) {
            RolePermission record = new RolePermission();
            record.setRoleId(roleId);
            record.setPermissionId(Integer.valueOf(permissionId));
            rolePermissionService.save(record);
        }
        return R.ok();
    }

    @GetMapping(value = "/permission")
    @ApiOperation("分页获取菜单列表")
    public R permission(@RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                  @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<Permission> page = new Page<>(offset, limit);
        IPage<Permission> pageInfo = permissionService.page(page);
        PageVO<Permission> responseDto = new PageVO<>(pageInfo);
        return R.ok(responseDto);
    }

    @GetMapping(value = "/permissionTree")
    @ApiOperation("获取菜单结构")
    public R permissionTree(@RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                      @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<Permission> page = new Page<>(offset, limit);
        IPage<Permission> pageInfo = permissionService.page(page);
        List<Permission> permissions = pageInfo.getRecords();
        List<PermissionDTO> permissionDTOList = new ArrayList<>();
        for (Permission permission : permissions) {
            PermissionDTO permissionDto = new PermissionDTO();
            permissionDto.setId(permission.getPermissionId().toString());
            if (permission.getParentPermissionId() != null) {
                permissionDto.setPId(permission.getParentPermissionId().toString());
            }
            permissionDto.setName(permission.getPermissionName());
            permissionDTOList.add(permissionDto);
        }
        PageVO<PermissionDTO> responseDto = new PageVO<>();
        responseDto.setItems(permissionDTOList);
        responseDto.setTotal((int) pageInfo.getTotal());
        return R.ok(responseDto);
    }

    @GetMapping("/permission/add")
    @ApiOperation("增加菜单权限")
    public R addPermission(Permission permission) {
        permission.setCreateTime(new Date());
        permission.setUpdateTime(new Date());
        permissionService.save(permission);
        return R.ok();
    }

    @GetMapping(value = "/permission/delete/{permissionId}")
    @ApiOperation("删除菜单")
    public R deletePermission(@PathVariable("permissionId") int permissionId) {
        permissionService.removeById(permissionId);
        return R.ok();
    }

}
