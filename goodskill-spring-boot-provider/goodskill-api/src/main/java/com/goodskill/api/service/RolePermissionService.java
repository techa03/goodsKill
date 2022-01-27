package com.goodskill.api.service;

import java.util.List;

/**
 * @author heng
 */
public interface RolePermissionService<T> {

    boolean remove(int id);

    boolean save(T record);

    List<T> list();

    List<T> list(Integer id);
}
