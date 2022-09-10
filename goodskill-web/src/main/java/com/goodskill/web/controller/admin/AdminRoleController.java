package com.goodskill.web.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Tag(name = "后台角色权限管理")
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

    @Operation(summary ="分页查询角色")
    @RequestMapping(value = "/role/list", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public R<PageVO<Role>> role(
            @Parameter(name = "分页当前页码")
            @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
            @Parameter(name = "分页每页显示数量")
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<Role> page = new Page<>(offset, limit);
        IPage<Role> pageInfo = roleService.page(page);
        PageVO<Role> pageVO = new PageVO<>(pageInfo);
        return R.ok(pageVO);
    }

    @Operation(summary ="分页查询角色部分信息")
    @RequestMapping(value = "/role/less", method = GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public R<PageVO<RoleDTO>> roleLess(
            @Parameter(name = "分页当前页码")
            @RequestParam(name = "page", required = false, defaultValue = "0") int offset,
            @Parameter(name = "分页每页显示数量")
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

    @Operation(summary ="新增角色")
    @GetMapping("/role/add")
    public R addRole(Role role) {
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        roleService.save(role);
        return R.ok();
    }

    @GetMapping(value = "/role/delete/{roleId}")
    @Operation(summary ="删除角色")
    public R deleteRole(@PathVariable("roleId") int roleId) {
        Role entity = new Role();
        entity.setRoleId(roleId);
        roleService.remove(entity);
        return R.ok();
    }

    @GetMapping(value = "/role/routes")
    @Operation(summary ="获取权限路由")
    public R listRoutes() {
        String route = "[{\"path\":\"/redirect\",\"component\":\"layout/Layout\",\"hidden\":true,\"children\":[{\"path\":\"/redirect/:path*\",\"component\":\"views/redirect/index\"}]},{\"path\":\"/login\",\"component\":\"views/login/index\",\"hidden\":true},{\"path\":\"/auth-redirect\",\"component\":\"views/login/auth-redirect\",\"hidden\":true},{\"path\":\"/404\",\"component\":\"views/error-page/404\",\"hidden\":true},{\"path\":\"/401\",\"component\":\"views/error-page/401\",\"hidden\":true},{\"path\":\"\",\"component\":\"layout/Layout\",\"redirect\":\"dashboard\",\"children\":[{\"path\":\"dashboard\",\"component\":\"views/dashboard/index\",\"name\":\"Dashboard\",\"meta\":{\"title\":\"Dashboard\",\"icon\":\"dashboard\",\"affix\":true}}]},{\"path\":\"/documentation\",\"component\":\"layout/Layout\",\"children\":[{\"path\":\"index\",\"component\":\"views/documentation/index\",\"name\":\"Documentation\",\"meta\":{\"title\":\"Documentation\",\"icon\":\"documentation\",\"affix\":true}}]},{\"path\":\"/guide\",\"component\":\"layout/Layout\",\"redirect\":\"/guide/index\",\"children\":[{\"path\":\"index\",\"component\":\"views/guide/index\",\"name\":\"Guide\",\"meta\":{\"title\":\"Guide\",\"icon\":\"guide\",\"noCache\":true}}]},{\"path\":\"/permission\",\"component\":\"layout/Layout\",\"redirect\":\"/permission/index\",\"alwaysShow\":true,\"meta\":{\"title\":\"Permission\",\"icon\":\"lock\",\"roles\":[\"admin\",\"editor\"]},\"children\":[{\"path\":\"page\",\"component\":\"views/permission/page\",\"name\":\"PagePermission\",\"meta\":{\"title\":\"Page Permission\",\"roles\":[\"admin\"]}},{\"path\":\"directive\",\"component\":\"views/permission/directive\",\"name\":\"DirectivePermission\",\"meta\":{\"title\":\"Directive Permission\"}},{\"path\":\"role\",\"component\":\"views/permission/role\",\"name\":\"RolePermission\",\"meta\":{\"title\":\"Role Permission\",\"roles\":[\"admin\"]}}]},{\"path\":\"/icon\",\"component\":\"layout/Layout\",\"children\":[{\"path\":\"index\",\"component\":\"views/icons/index\",\"name\":\"Icons\",\"meta\":{\"title\":\"Icons\",\"icon\":\"icon\",\"noCache\":true}}]},{\"path\":\"/components\",\"component\":\"layout/Layout\",\"redirect\":\"noRedirect\",\"name\":\"ComponentDemo\",\"meta\":{\"title\":\"Components\",\"icon\":\"component\"},\"children\":[{\"path\":\"tinymce\",\"component\":\"views/components-demo/tinymce\",\"name\":\"TinymceDemo\",\"meta\":{\"title\":\"Tinymce\"}},{\"path\":\"markdown\",\"component\":\"views/components-demo/markdown\",\"name\":\"MarkdownDemo\",\"meta\":{\"title\":\"Markdown\"}},{\"path\":\"json-editor\",\"component\":\"views/components-demo/json-editor\",\"name\":\"JsonEditorDemo\",\"meta\":{\"title\":\"Json Editor\"}},{\"path\":\"split-pane\",\"component\":\"views/components-demo/split-pane\",\"name\":\"SplitpaneDemo\",\"meta\":{\"title\":\"SplitPane\"}},{\"path\":\"avatar-upload\",\"component\":\"views/components-demo/avatar-upload\",\"name\":\"AvatarUploadDemo\",\"meta\":{\"title\":\"Avatar Upload\"}},{\"path\":\"dropzone\",\"component\":\"views/components-demo/dropzone\",\"name\":\"DropzoneDemo\",\"meta\":{\"title\":\"Dropzone\"}},{\"path\":\"sticky\",\"component\":\"views/components-demo/sticky\",\"name\":\"StickyDemo\",\"meta\":{\"title\":\"Sticky\"}},{\"path\":\"count-to\",\"component\":\"views/components-demo/count-to\",\"name\":\"CountToDemo\",\"meta\":{\"title\":\"Count To\"}},{\"path\":\"mixin\",\"component\":\"views/components-demo/mixin\",\"name\":\"ComponentMixinDemo\",\"meta\":{\"title\":\"componentMixin\"}},{\"path\":\"back-to-top\",\"component\":\"views/components-demo/back-to-top\",\"name\":\"BackToTopDemo\",\"meta\":{\"title\":\"Back To Top\"}},{\"path\":\"drag-dialog\",\"component\":\"views/components-demo/drag-dialog\",\"name\":\"DragDialogDemo\",\"meta\":{\"title\":\"Drag Dialog\"}},{\"path\":\"drag-select\",\"component\":\"views/components-demo/drag-select\",\"name\":\"DragSelectDemo\",\"meta\":{\"title\":\"Drag Select\"}},{\"path\":\"dnd-list\",\"component\":\"views/components-demo/dnd-list\",\"name\":\"DndListDemo\",\"meta\":{\"title\":\"Dnd List\"}},{\"path\":\"drag-kanban\",\"component\":\"views/components-demo/drag-kanban\",\"name\":\"DragKanbanDemo\",\"meta\":{\"title\":\"Drag Kanban\"}}]},{\"path\":\"/charts\",\"component\":\"layout/Layout\",\"redirect\":\"noRedirect\",\"name\":\"Charts\",\"meta\":{\"title\":\"Charts\",\"icon\":\"chart\"},\"children\":[{\"path\":\"keyboard\",\"component\":\"views/charts/keyboard\",\"name\":\"KeyboardChart\",\"meta\":{\"title\":\"Keyboard Chart\",\"noCache\":true}},{\"path\":\"line\",\"component\":\"views/charts/line\",\"name\":\"LineChart\",\"meta\":{\"title\":\"Line Chart\",\"noCache\":true}},{\"path\":\"mixchart\",\"component\":\"views/charts/mixChart\",\"name\":\"MixChart\",\"meta\":{\"title\":\"Mix Chart\",\"noCache\":true}}]},{\"path\":\"/nested\",\"component\":\"layout/Layout\",\"redirect\":\"/nested/menu1/menu1-1\",\"name\":\"Nested\",\"meta\":{\"title\":\"Nested\",\"icon\":\"nested\"},\"children\":[{\"path\":\"menu1\",\"component\":\"views/nested/menu1/index\",\"name\":\"Menu1\",\"meta\":{\"title\":\"Menu1\"},\"redirect\":\"/nested/menu1/menu1-1\",\"children\":[{\"path\":\"menu1-1\",\"component\":\"views/nested/menu1/menu1-1\",\"name\":\"Menu1-1\",\"meta\":{\"title\":\"Menu1-1\"}},{\"path\":\"menu1-2\",\"component\":\"views/nested/menu1/menu1-2\",\"name\":\"Menu1-2\",\"redirect\":\"/nested/menu1/menu1-2/menu1-2-1\",\"meta\":{\"title\":\"Menu1-2\"},\"children\":[{\"path\":\"menu1-2-1\",\"component\":\"views/nested/menu1/menu1-2/menu1-2-1\",\"name\":\"Menu1-2-1\",\"meta\":{\"title\":\"Menu1-2-1\"}},{\"path\":\"menu1-2-2\",\"component\":\"views/nested/menu1/menu1-2/menu1-2-2\",\"name\":\"Menu1-2-2\",\"meta\":{\"title\":\"Menu1-2-2\"}}]},{\"path\":\"menu1-3\",\"component\":\"views/nested/menu1/menu1-3\",\"name\":\"Menu1-3\",\"meta\":{\"title\":\"Menu1-3\"}}]},{\"path\":\"menu2\",\"name\":\"Menu2\",\"component\":\"views/nested/menu2/index\",\"meta\":{\"title\":\"Menu2\"}}]},{\"path\":\"/example\",\"component\":\"layout/Layout\",\"redirect\":\"/example/list\",\"name\":\"Example\",\"meta\":{\"title\":\"Example\",\"icon\":\"example\"},\"children\":[{\"path\":\"create\",\"component\":\"views/example/create\",\"name\":\"CreateArticle\",\"meta\":{\"title\":\"Create Article\",\"icon\":\"edit\"}},{\"path\":\"edit/:id(\\\\d+)\",\"component\":\"views/example/edit\",\"name\":\"EditArticle\",\"meta\":{\"title\":\"Edit Article\",\"noCache\":true},\"hidden\":true},{\"path\":\"list\",\"component\":\"views/example/list\",\"name\":\"ArticleList\",\"meta\":{\"title\":\"Article List\",\"icon\":\"list\"}}]},{\"path\":\"/tab\",\"component\":\"layout/Layout\",\"children\":[{\"path\":\"index\",\"component\":\"views/tab/index\",\"name\":\"Tab\",\"meta\":{\"title\":\"Tab\",\"icon\":\"tab\"}}]},{\"path\":\"/error\",\"component\":\"layout/Layout\",\"redirect\":\"noRedirect\",\"name\":\"ErrorPages\",\"meta\":{\"title\":\"Error Pages\",\"icon\":\"404\"},\"children\":[{\"path\":\"401\",\"component\":\"views/error-page/401\",\"name\":\"Page401\",\"meta\":{\"title\":\"Page 401\",\"noCache\":true}},{\"path\":\"404\",\"component\":\"views/error-page/404\",\"name\":\"Page404\",\"meta\":{\"title\":\"Page 404\",\"noCache\":true}}]},{\"path\":\"/error-log\",\"component\":\"layout/Layout\",\"redirect\":\"noRedirect\",\"children\":[{\"path\":\"log\",\"component\":\"views/error-log/index\",\"name\":\"ErrorLog\",\"meta\":{\"title\":\"Error Log\",\"icon\":\"bug\"}}]},{\"path\":\"/excel\",\"component\":\"layout/Layout\",\"redirect\":\"/excel/export-excel\",\"name\":\"Excel\",\"meta\":{\"title\":\"Excel\",\"icon\":\"excel\"},\"children\":[{\"path\":\"export-excel\",\"component\":\"views/excel/export-excel\",\"name\":\"ExportExcel\",\"meta\":{\"title\":\"Export Excel\"}},{\"path\":\"export-selected-excel\",\"component\":\"views/excel/select-excel\",\"name\":\"SelectExcel\",\"meta\":{\"title\":\"Select Excel\"}},{\"path\":\"export-merge-header\",\"component\":\"views/excel/merge-header\",\"name\":\"MergeHeader\",\"meta\":{\"title\":\"Merge Header\"}},{\"path\":\"upload-excel\",\"component\":\"views/excel/upload-excel\",\"name\":\"UploadExcel\",\"meta\":{\"title\":\"Upload Excel\"}}]},{\"path\":\"/zip\",\"component\":\"layout/Layout\",\"redirect\":\"/zip/download\",\"alwaysShow\":true,\"meta\":{\"title\":\"Zip\",\"icon\":\"zip\"},\"children\":[{\"path\":\"download\",\"component\":\"views/zip/index\",\"name\":\"ExportZip\",\"meta\":{\"title\":\"Export Zip\"}}]},{\"path\":\"/pdf\",\"component\":\"layout/Layout\",\"redirect\":\"/pdf/index\",\"children\":[{\"path\":\"index\",\"component\":\"views/pdf/index\",\"name\":\"PDF\",\"meta\":{\"title\":\"PDF\",\"icon\":\"pdf\"}}]},{\"path\":\"/pdf/download\",\"component\":\"views/pdf/download\",\"hidden\":true},{\"path\":\"/theme\",\"component\":\"layout/Layout\",\"redirect\":\"noRedirect\",\"children\":[{\"path\":\"index\",\"component\":\"views/theme/index\",\"name\":\"Theme\",\"meta\":{\"title\":\"Theme\",\"icon\":\"theme\"}}]},{\"path\":\"/clipboard\",\"component\":\"layout/Layout\",\"redirect\":\"noRedirect\",\"children\":[{\"path\":\"index\",\"component\":\"views/clipboard/index\",\"name\":\"ClipboardDemo\",\"meta\":{\"title\":\"Clipboard Demo\",\"icon\":\"clipboard\"}}]},{\"path\":\"/i18n\",\"component\":\"layout/Layout\",\"children\":[{\"path\":\"index\",\"component\":\"views/i18n-demo/index\",\"name\":\"I18n\",\"meta\":{\"title\":\"I18n\",\"icon\":\"international\"}}]},{\"path\":\"external-link\",\"component\":\"layout/Layout\",\"children\":[{\"path\":\"https://github.com/PanJiaChen/vue-element-admin\",\"meta\":{\"title\":\"External Link\",\"icon\":\"link\"}}]},{\"path\":\"*\",\"redirect\":\"/404\",\"hidden\":true}]";
        JSONArray objects = JSON.parseArray(route);
        return R.ok(objects);
    }

    @PostMapping(value = "/role/permissions/update/{roleId}")
    @Transactional(rollbackFor = Exception.class)
    @Operation(summary ="更新角色权限")
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
    @Operation(summary ="分页获取菜单列表")
    public R permission(@RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                                  @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<Permission> page = new Page<>(offset, limit);
        IPage<Permission> pageInfo = permissionService.page(page);
        PageVO<Permission> responseDto = new PageVO<>(pageInfo);
        return R.ok(responseDto);
    }

    @GetMapping(value = "/permissionTree")
    @Operation(summary ="获取菜单结构")
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
    @Operation(summary ="增加菜单权限")
    public R addPermission(Permission permission) {
        permission.setCreateTime(new Date());
        permission.setUpdateTime(new Date());
        permissionService.save(permission);
        return R.ok();
    }

    @GetMapping(value = "/permission/delete/{permissionId}")
    @Operation(summary ="删除菜单")
    public R deletePermission(@PathVariable("permissionId") int permissionId) {
        permissionService.removeById(permissionId);
        return R.ok();
    }

}
