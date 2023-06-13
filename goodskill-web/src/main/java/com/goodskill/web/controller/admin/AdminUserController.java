package com.goodskill.web.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.api.service.UserAccountService;
import com.goodskill.api.service.UserRoleService;
import com.goodskill.common.enums.CommonConstants;
import com.goodskill.common.info.R;
import com.goodskill.common.util.JwtUtils;
import com.goodskill.entity.User;
import com.goodskill.entity.UserRole;
import com.goodskill.web.dto.RoleDTO;
import com.goodskill.web.vo.PageVO;
import com.goodskill.web.vo.UserVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Tag(name = "后台用户管理")
@RestController
@RequestMapping("/admin/user")
@Slf4j
public class AdminUserController {
    @DubboReference
    private UserAccountService userAccountService;
    @DubboReference
    private UserRoleService userRoleService;

    @PostMapping(value = "/login")
    public R<?> login(@RequestBody User user) {
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        Subject subject = SecurityUtils.getSubject();

        Session session = subject.getSession();
        try {
            subject.login(token);
            session.setAttribute("user", user);
        } catch (AuthenticationException e1) {
            return R.fail("服务器内部错误！");
        } catch (Exception e) {
            session.setAttribute("user", null);
            return R.fail("用户名或密码错误！");
        }
        user.setPassword(null);
        Map map = Maps.newHashMap();
        map.put("token", JwtUtils.createToken(BeanUtil.beanToMap(user)));
        return R.ok(map);
    }

    @GetMapping(value = "/info")
    public R<UserVO> getUserInfo(@RequestParam String token) {
        Map<String, Object> userMap = JwtUtils.parseToken(token);
        User user = BeanUtil.toBean(userMap, User.class);
        User byUserAccount = userAccountService.findByUserAccount(user.getUsername());
        if (byUserAccount != null) {
            UserVO userVO = new UserVO();
            BeanUtil.copyProperties(byUserAccount, userVO);
            if (!StringUtils.hasText(userVO.getAvatar())) {
                userVO.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            }
            userVO.setRoles(Lists.newArrayList("admin"));
            userVO.setName(byUserAccount.getUsername());
            userVO.setIntroduction("demo");
            return R.ok(userVO);
        }
        return R.fail("未获取到用户信息");
    }

    @PostMapping(value = "/logout")
    public R<Void> logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return R.ok();
    }

    @GetMapping(value = "/list")
    public R<PageVO> user(@RequestParam(name = "page", required = false, defaultValue = "0") int offset,
                  @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        Page<User> page = new Page<>(offset, limit);
        IPage<User> pageInfo = userAccountService.page(page);
        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setTotal((int) pageInfo.getTotal());
        List<UserVO> userVOS = BeanUtil.copyToList(pageInfo.getRecords(), UserVO.class);
        userVOS.forEach(it -> {
            String s = Arrays.stream(CommonConstants.UserAccountStatusEnum.values())
                    .filter(va -> Objects.equals(it.getLocked(), String.valueOf(va.getCode())))
                    .findFirst().map(CommonConstants.UserAccountStatusEnum::getDesc).orElseGet(null);
            it.setLocked(s);
        });
        pageVO.setItems(userVOS);
        return R.ok(pageVO);
    }

    @DeleteMapping(value = "/delete/{userId}")
    public R deleteUser(@PathVariable("userId") int userId) {
        userAccountService.removeById(userId);
        return R.ok();
    }

    @PostMapping(value = "/addRole/{userId}")
    @Transactional(rollbackFor = Exception.class)
    public R addRole(@PathVariable("userId") int userId, @RequestBody RoleDTO[] roleDto) {
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
        return R.ok();
    }
}
