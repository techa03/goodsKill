package org.seckill.dao;


import java.util.List;

/**
 * Created by heng on 17/1/18.
 */
public interface BaseDao<T extends Object> {
    int insert(T t);

    int delete(long id);

    T selectByPrimaryKey(long id);

    List<T> selectBySelective(T t);
}
