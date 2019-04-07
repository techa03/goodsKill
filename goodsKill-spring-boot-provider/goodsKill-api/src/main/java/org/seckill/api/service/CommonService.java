package org.seckill.api.service;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.seckill.api.annotation.BaseService;

import java.util.List;

@BaseService
public interface CommonService<Example,Entity> {

    PageInfo selectByPage(Example example, int pageNum, int pageSize);

    /**
     * 根据条件查询记录数量
     * @param example
     * @return
     */
    int countByExample(Example example);

    /**
     * 根据条件删除记录
     * @param example
     * @return
     */
    int deleteByExample(Example example);

    /**
     * 根据主键删除记录
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入记录
     * @param entity
     * @return
     */
    int insert(Entity entity);

    /**
     * 插入记录有效字段
     * @param entity
     * @return
     */
    int insertSelective(Entity entity);

    /**
     * 根据条件查询记录，附带BLOB字段
     * @param example
     * @return
     */
    List<Entity> selectByExampleWithBLOBs(Example example);

    /**
     * 根据条件查询记录
     * @param example
     * @return
     */
    List<Entity> selectByExample(Example example);

    /**
     * 根据条件查询记录并按页码分页，附带BLOB字段
     * @param example 条件
     * @param pageNum 页数
     * @param pageSize 每页记录数
     * @return
     */
    List<Entity> selectByExampleWithBLOBsForStartPage(Example example, Integer pageNum, Integer pageSize);

    /**
     * 根据条件查询记录并按页码分页
     * @param example 条件
     * @param pageNum 页数
     * @param pageSize 每页记录数
     * @return
     */
    List<Entity> selectByExampleForStartPage(Example example, Integer pageNum, Integer pageSize);

    /**
     * 根据条件查询记录并按最后记录数分页，附带BLOB字段
     * @param example 条件
     * @param offset 跳过数量
     * @param limit 查询数量
     * @return
     */
    List<Entity> selectByExampleWithBLOBsForOffsetPage(Example example, Integer offset, Integer limit);

    /**
     * 根据条件查询记录并按最后记录数分页
     * @param example 条件
     * @param offset 跳过数量
     * @param limit 查询数量
     * @return
     */
    List<Entity> selectByExampleForOffsetPage(Example example, Integer offset, Integer limit);

    /**
     * 根据条件查询第一条记录
     * @param example
     * @return
     */
    Entity selectFirstByExample(Example example);

    /**
     * 根据条件查询第一条记录，附带BLOB字段
     * @param example
     * @return
     */
    Entity selectFirstByExampleWithBLOBs(Example example);

    /**
     * 根据主键查询记录
     * @param id
     * @return
     */
    Entity selectByPrimaryKey(Integer id);

    /**
     * 根据条件更新有效字段
     * @param entity
     * @param example
     * @return
     */
    int updateByExampleSelective(@Param("entity") Entity entity, @Param("example") Example example);

    /**
     * 根据条件更新记录有效字段，附带BLOB字段
     * @param entity
     * @param example
     * @return
     */
    int updateByExampleWithBLOBs(@Param("entity") Entity entity, @Param("example") Example example);

    /**
     * 根据条件更新记录
     * @param entity
     * @param example
     * @return
     */
    int updateByExample(@Param("entity") Entity entity, @Param("example") Example example);

    /**
     * 根据主键更新记录有效字段
     * @param entity
     * @return
     */
    int updateByPrimaryKeySelective(Entity entity);

    /**
     * 根据主键更新记录，附带BLOB字段
     * @param entity
     * @return
     */
    int updateByPrimaryKeyWithBLOBs(Entity entity);

    /**
     * 根据主键更新记录
     * @param entity
     * @return
     */
    int updateByPrimaryKey(Entity entity);

    /**
     * 根据主键批量删除记录
     * @param ids
     * @return
     */
    int deleteByPrimaryKeys(String ids);

}
