package org.seckill.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.seckill.api.service.*;
import org.seckill.entity.*;
import org.seckill.web.dto.PermissionDto;
import org.seckill.web.dto.ResponseDto;
import org.seckill.web.dto.RoleDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(tags = "角色权限管理")
@Controller
@RequestMapping("/seckill/admin")
@Slf4j
public class AdminController {
    @Reference
    private UserAccountService userService;
    @Reference
    private RoleService roleService;
    @Reference
    private UserRoleService userRoleService;
    @Reference
    private PermissionService permissionService;
    @Reference
    private RolePermissionService rolePermissionService;

    @RequestMapping(value = "/role", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto role(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page page = new Page(offset, limit);
        IPage<Role> pageInfo = roleService.page(page);
        ResponseDto<Role> responseDto = new ResponseDto<>();
        responseDto.setData(pageInfo.getRecords().toArray(new Role[pageInfo.getRecords().size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @RequestMapping(value = "/roleLess", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto roleLess(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page page = new Page(offset, limit);
        IPage<Role> pageInfo = roleService.page(page);
        ResponseDto<RoleDto> responseDto = new ResponseDto<>();
        List<Role> list = pageInfo.getRecords();
        List<RoleDto> result = new ArrayList();
        for (Role role : list) {
            RoleDto roleDto = new RoleDto();
            BeanUtils.copyProperties(role, roleDto);
            result.add(roleDto);
        }
        responseDto.setData(result.toArray(new RoleDto[result.size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @GetMapping("/role/add")
    public String addRole(Role role) {
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        roleService.save(role);
        return "redirect:/html/admin/role.html";
    }

    @RequestMapping(value = "/role/delete/{roleId}", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto deleteRole(@PathVariable("roleId") int roleId) {
        Role entity = new Role();
        entity.setRoleId(roleId);
        roleService.remove(entity);
        ResponseDto<Role> responseDto = new ResponseDto<>();
        return responseDto;
    }

    @RequestMapping(value = "/permission", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto permission(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                  @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page page = new Page(offset, limit);
        IPage<Permission> pageInfo = permissionService.page(page);
        ResponseDto<Permission> responseDto = new ResponseDto<>();
        responseDto.setData(pageInfo.getRecords().toArray(new Permission[pageInfo.getRecords().size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @RequestMapping(value = "/permissionTree", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto permissionTree(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                      @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page page = new Page(offset, limit);
        IPage<Permission> pageInfo = permissionService.page(page);
        List<Permission> permissions = pageInfo.getRecords();
        List<PermissionDto> permissionDtoList = new ArrayList<>();
        for (Permission permission : permissions) {
            PermissionDto permissionDto = new PermissionDto();
            permissionDto.setId(permission.getPermissionId().toString());
            if (permission.getParentPermissionId() != null) {
                permissionDto.setPId(permission.getParentPermissionId().toString());
            }
            permissionDto.setName(permission.getPermissionName());
            permissionDtoList.add(permissionDto);
        }
        ResponseDto<PermissionDto> responseDto = new ResponseDto<>();
        responseDto.setData(permissionDtoList.toArray(new PermissionDto[permissionDtoList.size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @GetMapping("/permission/add")
    public String addPermission(Permission permission) {
        permission.setCreateTime(new Date());
        permission.setUpdateTime(new Date());
        permissionService.save(permission);
        return "redirect:/html/admin/permission.html";
    }

    @RequestMapping(value = "/permission/delete/{permissionId}", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto deletePermission(@PathVariable("permissionId") int permissionId) {
        permissionService.removeById(permissionId);
        ResponseDto<Permission> responseDto = new ResponseDto<>();
        return responseDto;
    }

    @RequestMapping(value = "/user", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto user(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page page = new Page(offset, limit);
        IPage<User> pageInfo = userService.page(page);
        ResponseDto<User> responseDto = new ResponseDto<>();
        responseDto.setData(pageInfo.getRecords().toArray(new User[pageInfo.getRecords().size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @RequestMapping(value = "/user/delete/{userId}", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto deleteUser(@PathVariable("userId") int userId) {
        userService.removeById(userId);
        ResponseDto<User> responseDto = new ResponseDto<>();
        return responseDto;
    }

    @RequestMapping(value = "/user/{userId}/addRole", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional
    public ResponseDto addRole(@PathVariable("userId") int userId, @RequestBody RoleDto[] roleDto) {
        for (RoleDto dto : roleDto) {
            UserRole record = new UserRole();
            record.setUserId(userId);
            record.setRoleId(dto.getRoleId());
            UserRole entity = new UserRole();
            entity.setUserId(userId);
            entity.setRoleId(dto.getRoleId());
            userRoleService.remove(entity);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            userRoleService.save(record);
        }
        ResponseDto<User> responseDto = new ResponseDto<>();
        return responseDto;
    }

    @RequestMapping(value = "/role/{roleId}/updateRolePermission", method = POST, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional
    public ResponseDto updateRolePermission(@PathVariable("roleId") int roleId, @RequestBody String[] permissionIds) {
        rolePermissionService.remove(roleId);
        for (String permissionId : permissionIds) {
            RolePermission record = new RolePermission();
            record.setRoleId(roleId);
            record.setPermissionId(Integer.valueOf(permissionId));
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            rolePermissionService.save(record);
        }
        ResponseDto<User> responseDto = new ResponseDto<>();
        return responseDto;
    }

}
