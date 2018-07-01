package org.seckill.api.service;

import org.seckill.entity.Permission;
import org.seckill.entity.User;
import org.seckill.api.exception.SeckillException;
import org.seckill.entity.Role;

import java.util.Set;


public interface UserAccountService{
	void register(User user);

	void login(User user) throws SeckillException;

	Set<Role> findRoles(String username);

	Set<Permission> findPermissions(String username);

	User findByUserAccount(String username);
}
