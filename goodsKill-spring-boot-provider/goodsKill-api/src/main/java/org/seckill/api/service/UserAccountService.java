package org.seckill.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.seckill.api.exception.SeckillException;
import org.seckill.entity.Permission;
import org.seckill.entity.Role;
import org.seckill.entity.User;

import java.util.Set;


/**
 * @author heng
 */
public interface UserAccountService extends IService<User> {
	void register(User user);

	void login(User user) throws SeckillException;

	Set<Role> findRoles(String username);

	Set<Permission> findPermissions(String username);

	User findByUserAccount(String username);

    PageInfo<User> getSeckillList(int pageNum, int pageSize);
}
