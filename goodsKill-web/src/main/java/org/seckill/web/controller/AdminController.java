package org.seckill.web.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.seckill.api.service.*;
import org.seckill.entity.*;
import org.seckill.web.dto.PermissionDto;
import org.seckill.web.dto.ResponseDto;
import org.seckill.web.dto.RoleDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "角色权限管理")
@Controller
@RequestMapping("/seckill/admin")
@Slf4j
public class AdminController {
    @Autowired
    UserAccountService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    RolePermissionService rolePermissionService;

    @GetMapping("/role")
    @ResponseBody
    public ResponseDto role(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        PageInfo<Role> pageInfo = roleService.selectByPage(new RoleExample(), offset, limit);
        ResponseDto<Role> responseDto = new ResponseDto<>();
        responseDto.setData(pageInfo.getList().toArray(new Role[pageInfo.getList().size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @GetMapping("/roleLess")
    @ResponseBody
    public ResponseDto roleLess(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        PageInfo<Role> pageInfo = roleService.selectByPage(new RoleExample(), offset, limit);
        ResponseDto<RoleDto> responseDto = new ResponseDto<>();
        List<Role> list = pageInfo.getList();
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
        roleService.insert(role);
        return "redirect:/html/admin/role.html";
    }

    @RequestMapping(value = "/role/delete/{roleId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseDto deleteRole(@PathVariable("roleId") int roleId) {
        int count = roleService.deleteByPrimaryKey(roleId);
        ResponseDto<Role> responseDto = new ResponseDto<>();
        responseDto.setCount(count);
        return responseDto;
    }

    @GetMapping("/permission")
    @ResponseBody
    public ResponseDto permission(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                  @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        PageInfo<Permission> pageInfo = permissionService.selectByPage(new PermissionExample(), offset, limit);
        ResponseDto<Permission> responseDto = new ResponseDto<>();
        responseDto.setData(pageInfo.getList().toArray(new Permission[pageInfo.getList().size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @GetMapping("/permissionTree")
    @ResponseBody
    public ResponseDto permissionTree(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                      @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        PageInfo<Permission> pageInfo = permissionService.selectByPage(new PermissionExample(), offset, limit);
        List<Permission> permissions = pageInfo.getList();
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
        permissionService.insert(permission);
        return "redirect:/html/admin/permission.html";
    }

    @RequestMapping(value = "/permission/delete/{permissionId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseDto deletePermission(@PathVariable("permissionId") int permissionId) {
        int count = permissionService.deleteByPrimaryKey(permissionId);
        ResponseDto<Permission> responseDto = new ResponseDto<>();
        responseDto.setCount(count);
        return responseDto;
    }

    @GetMapping("/user")
    @ResponseBody
    public ResponseDto user(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        PageInfo<User> pageInfo = userService.selectByPage(new UserExample(), offset, limit);
        ResponseDto<User> responseDto = new ResponseDto<>();
        responseDto.setData(pageInfo.getList().toArray(new User[pageInfo.getList().size()]));
        responseDto.setCount((int) pageInfo.getTotal());
        return responseDto;
    }

    @RequestMapping(value = "/user/delete/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseDto deleteUser(@PathVariable("userId") int userId) {
        int count = userService.deleteByPrimaryKey(userId);
        ResponseDto<User> responseDto = new ResponseDto<>();
        responseDto.setCount(count);
        return responseDto;
    }

    @PostMapping(value = "/user/{userId}/addRole")
    @ResponseBody
    @Transactional
    public ResponseDto addRole(@PathVariable("userId") int userId, @RequestBody RoleDto[] roleDto) {
        UserRoleExample example = new UserRoleExample();
        for (RoleDto dto : roleDto) {
            example.clear();
            UserRole record = new UserRole();
            record.setUserId(userId);
            record.setRoleId(dto.getRoleId());
            example.createCriteria().andUserIdEqualTo(userId).andRoleIdEqualTo(dto.getRoleId());
            userRoleService.deleteByExample(example);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            userRoleService.insertSelective(record);
        }
        ResponseDto<User> responseDto = new ResponseDto<>();
        return responseDto;
    }

    @PostMapping(value = "/role/{roleId}/updateRolePermission")
    @ResponseBody
    @Transactional
    public ResponseDto updateRolePermission(@PathVariable("roleId") int roleId, @RequestBody String[] permissionIds) {
        UserRoleExample example = new UserRoleExample();
        RolePermissionExample rolePermissionExample = new RolePermissionExample();
        rolePermissionExample.createCriteria().andRoleIdEqualTo(roleId);
        rolePermissionService.deleteByExample(rolePermissionExample);
        for (String permissionId : permissionIds) {
            RolePermission record = new RolePermission();
            record.setRoleId(roleId);
            record.setPermissionId(Integer.valueOf(permissionId));
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            rolePermissionService.insertSelective(record);
        }
        ResponseDto<User> responseDto = new ResponseDto<>();
        return responseDto;
    }

}
