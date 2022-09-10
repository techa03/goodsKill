package com.goodskill.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.api.service.*;
import com.goodskill.entity.*;
import com.goodskill.web.dto.PermissionDTO;
import com.goodskill.web.dto.ResponseDTO;
import com.goodskill.web.dto.RoleDTO;
import com.goodskill.web.util.HttpUrlUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Tag(name = "角色权限管理", description = "角色权限管理")
@Controller
@RequestMapping("/seckill/admin")
@Slf4j
public class AdminController {
    @DubboReference
    private UserAccountService userService;
    @DubboReference
    private RoleService roleService;
    @DubboReference
    private UserRoleService userRoleService;
    @DubboReference
    private PermissionService permissionService;
    @DubboReference
    private RolePermissionService rolePermissionService;

    @Operation(summary = "分页查询角色")
    @RequestMapping(value = "/role", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO role(
            @Parameter(description = "分页当前页码")
            @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
            @Parameter(description = "分页每页显示数量")
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<Role> page = new Page<>(offset, limit);
        IPage<Role> pageInfo = roleService.page(page);
        ResponseDTO<Role> responseDto = new ResponseDTO<>();
        responseDto.setData(pageInfo.getRecords().toArray(new Role[pageInfo.getRecords().size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @Operation(summary = "分页查询角色部分信息")
    @RequestMapping(value = "/roleLess", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO roleLess(
            @Parameter(description = "分页当前页码")
            @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
            @Parameter(description = "分页每页显示数量")
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<Role> page = new Page<>(offset, limit);
        IPage<Role> pageInfo = roleService.page(page);
        ResponseDTO<RoleDTO> responseDto = new ResponseDTO<>();
        List<Role> list = pageInfo.getRecords();
        List<RoleDTO> result = new ArrayList();
        for (Role role : list) {
            RoleDTO roleDto = new RoleDTO();
            BeanUtils.copyProperties(role, roleDto);
            result.add(roleDto);
        }
        responseDto.setData(result.toArray(new RoleDTO[result.size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @Operation(summary = "新增角色")
    @GetMapping("/role/add")
    public String addRole(Role role) {
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        roleService.save(role);
        return HttpUrlUtil.replaceRedirectUrl("redirect:/html/admin/role.html");
    }

    @RequestMapping(value = "/role/delete/{roleId}", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO deleteRole(@PathVariable("roleId") int roleId) {
        Role entity = new Role();
        entity.setRoleId(roleId);
        roleService.remove(entity);
        ResponseDTO<Role> responseDto = new ResponseDTO<>();
        return responseDto;
    }

    @RequestMapping(value = "/permission", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO permission(@RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                  @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<Permission> page = new Page<>(offset, limit);
        IPage<Permission> pageInfo = permissionService.page(page);
        ResponseDTO<Permission> responseDto = new ResponseDTO<>();
        responseDto.setData(pageInfo.getRecords().toArray(new Permission[pageInfo.getRecords().size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @RequestMapping(value = "/permissionTree", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO permissionTree(@RequestParam(name = "page", required = false, defaultValue = "0") int offset,
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
        ResponseDTO<PermissionDTO> responseDto = new ResponseDTO<>();
        responseDto.setData(permissionDTOList.toArray(new PermissionDTO[permissionDTOList.size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @GetMapping("/permission/add")
    public String addPermission(Permission permission) {
        permission.setCreateTime(new Date());
        permission.setUpdateTime(new Date());
        permissionService.save(permission);
        return HttpUrlUtil.replaceRedirectUrl("redirect:/html/admin/permission.html");
    }

    @RequestMapping(value = "/permission/delete/{permissionId}", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO deletePermission(@PathVariable("permissionId") int permissionId) {
        permissionService.removeById(permissionId);
        ResponseDTO<Permission> responseDto = new ResponseDTO<>();
        return responseDto;
    }

    @RequestMapping(value = "/user", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO user(@RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<User> page = new Page<>(offset, limit);
        IPage<User> pageInfo = userService.page(page);
        ResponseDTO<User> responseDto = new ResponseDTO<>();
        responseDto.setData(pageInfo.getRecords().toArray(new User[pageInfo.getRecords().size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @RequestMapping(value = "/user/delete/{userId}", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO deleteUser(@PathVariable("userId") int userId) {
        userService.removeById(userId);
        ResponseDTO<User> responseDto = new ResponseDTO<>();
        return responseDto;
    }

    @RequestMapping(value = "/user/{userId}/addRole", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional
    public ResponseDTO addRole(@PathVariable("userId") int userId, @RequestBody RoleDTO[] roleDto) {
        for (RoleDTO dto : roleDto) {
            UserRole record = new UserRole();
            record.setUserId(userId);
            record.setRoleId(dto.getRoleId());
            UserRole entity = new UserRole();
            entity.setUserId(userId);
            entity.setRoleId(dto.getRoleId());
            userRoleService.remove(entity);
            userRoleService.save(record);
        }
        ResponseDTO<User> responseDto = new ResponseDTO<>();
        return responseDto;
    }

    @RequestMapping(value = "/role/{roleId}/updateRolePermission", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional
    public ResponseDTO updateRolePermission(@PathVariable("roleId") int roleId, @RequestBody String[] permissionIds) {
        rolePermissionService.remove(roleId);
        for (String permissionId : permissionIds) {
            RolePermission record = new RolePermission();
            record.setRoleId(roleId);
            record.setPermissionId(Integer.valueOf(permissionId));
            rolePermissionService.save(record);
        }
        ResponseDTO<User> responseDto = new ResponseDTO<>();
        return responseDto;
    }

}
