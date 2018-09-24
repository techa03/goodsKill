package org.seckill.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.seckill.api.annotation.BaseService;
import org.seckill.api.service.CommonService;
import org.seckill.service.util.ApplicationContextUtil;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Service
@BaseService
@Slf4j
public abstract class AbstractServiceImpl<Mapper, Example, Entity> implements CommonService<Example, Entity> {
    public Mapper mapper;

    /**
     * 通过反射提取公共查询方法
     *
     * @param example
     * @return
     */
    public List<Entity> selectByExample(Example example) {
        mapper = (Mapper) getMapper();
        List<Entity> obj = null;
        obj = getEntities((Example) example, (List<Entity>) obj);
        return obj;
    }

    /**
     * 通过反射提取公共分页查询方法
     *
     * @param example
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo selectByPage(Example example, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        mapper = (Mapper) getMapper();
        List<Entity> obj = null;
        obj = getEntities((Example) example, (List<Entity>) obj);
        return new PageInfo(obj);
    }

    @Override
    public int countByExample(Example example) {
        try {
            mapper = (Mapper) getMapper();
            Method countByExample = mapper.getClass().getDeclaredMethod("countByExample", example.getClass());
            Object result = countByExample.invoke(mapper, example);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int deleteByExample(Example example) {
        try {
            mapper = (Mapper) getMapper();
            Method deleteByExample = mapper.getClass().getDeclaredMethod("deleteByExample", example.getClass());
            Object result = deleteByExample.invoke(mapper, example);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        try {
            mapper = (Mapper) getMapper();
            Method deleteByPrimaryKey = mapper.getClass().getDeclaredMethod("deleteByPrimaryKey", id.getClass());
            Object result = deleteByPrimaryKey.invoke(mapper, id);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int insert(Entity record) {
        try {
            mapper = (Mapper) getMapper();
            Method insert = mapper.getClass().getDeclaredMethod("insert", record.getClass());
            Object result = insert.invoke(mapper, record);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int insertSelective(Entity record) {
        try {
            mapper = (Mapper) getMapper();
            Method insertSelective = mapper.getClass().getDeclaredMethod("insertSelective", record.getClass());
            Object result = insertSelective.invoke(mapper, record);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public List<Entity> selectByExampleWithBLOBs(Example example) {
        try {
            mapper = (Mapper) getMapper();
            Method selectByExampleWithBLOBs = mapper.getClass().getDeclaredMethod("selectByExampleWithBLOBs", example.getClass());
            Object result = selectByExampleWithBLOBs.invoke(mapper, example);
            return (List<Entity>) result;
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Entity> selectByExampleWithBLOBsForStartPage(Example example, Integer pageNum, Integer pageSize) {
        try {
            mapper = (Mapper) getMapper();
            Method selectByExampleWithBLOBs = mapper.getClass().getDeclaredMethod("selectByExampleWithBLOBs", example.getClass());
            PageHelper.startPage(pageNum, pageSize, false);
            Object result = selectByExampleWithBLOBs.invoke(mapper, example);
            return (List<Entity>) result;
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Entity> selectByExampleForStartPage(Example example, Integer pageNum, Integer pageSize) {
        try {
            mapper = (Mapper) getMapper();
            Method selectByExample = mapper.getClass().getDeclaredMethod("selectByExample", example.getClass());
            PageHelper.startPage(pageNum, pageSize, false);
            Object result = selectByExample.invoke(mapper, example);
            return (List<Entity>) result;
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Entity> selectByExampleWithBLOBsForOffsetPage(Example example, Integer offset, Integer limit) {
        try {
            mapper = (Mapper) getMapper();
            Method selectByExampleWithBLOBs = mapper.getClass().getDeclaredMethod("selectByExampleWithBLOBs", example.getClass());
            PageHelper.offsetPage(offset, limit, false);
            Object result = selectByExampleWithBLOBs.invoke(mapper, example);
            return (List<Entity>) result;
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Entity> selectByExampleForOffsetPage(Example example, Integer offset, Integer limit) {
        try {
            mapper = (Mapper) getMapper();
            Method selectByExample = mapper.getClass().getDeclaredMethod("selectByExample", example.getClass());
            PageHelper.offsetPage(offset, limit, false);
            Object result = selectByExample.invoke(mapper, example);
            return (List<Entity>) result;
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Entity selectFirstByExample(Example example) {
        try {
            mapper = (Mapper) getMapper();
            Method selectByExample = mapper.getClass().getDeclaredMethod("selectByExample", example.getClass());
            List<Entity> result = (List<Entity>) selectByExample.invoke(mapper, example);
            if (null != result && result.size() > 0) {
                return result.get(0);
            }
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Entity selectFirstByExampleWithBLOBs(Example example) {
        try {
            mapper = (Mapper) getMapper();
            Method selectByExampleWithBLOBs = mapper.getClass().getDeclaredMethod("selectByExampleWithBLOBs", example.getClass());
            List<Entity> result = (List<Entity>) selectByExampleWithBLOBs.invoke(mapper, example);
            if (null != result && result.size() > 0) {
                return result.get(0);
            }
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Entity selectByPrimaryKey(Integer id) {
        try {
            mapper = (Mapper) getMapper();
            Method selectByPrimaryKey = mapper.getClass().getDeclaredMethod("selectByPrimaryKey", id.getClass());
            Object result = selectByPrimaryKey.invoke(mapper, id);
            return (Entity) result;
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            Method selectByPrimaryKey = null;
            try {
                selectByPrimaryKey = mapper.getClass().getDeclaredMethod("selectByPrimaryKey", Long.class);
                Object result = selectByPrimaryKey.invoke(mapper, Long.valueOf(id));
                return (Entity) result;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
                log.warn(e.getMessage(), e);
            }
        }

        return null;
    }

    @Override
    public int updateByExampleSelective(@Param("record") Entity record, @Param("example") Example example) {
        try {
            mapper = (Mapper) getMapper();
            Method updateByExampleSelective = mapper.getClass().getDeclaredMethod("updateByExampleSelective", record.getClass(), example.getClass());
            Object result = updateByExampleSelective.invoke(mapper, record, example);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public int updateByExampleWithBLOBs(@Param("record") Entity record, @Param("example") Example example) {
        try {
            mapper = (Mapper) getMapper();
            Method updateByExampleWithBLOBs = mapper.getClass().getDeclaredMethod("updateByExampleWithBLOBs", record.getClass(), example.getClass());
            Object result = updateByExampleWithBLOBs.invoke(mapper, record, example);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public int updateByExample(@Param("record") Entity record, @Param("example") Example example) {
        try {
            mapper = (Mapper) getMapper();
            Method updateByExample = mapper.getClass().getDeclaredMethod("updateByExample", record.getClass(), example.getClass());
            Object result = updateByExample.invoke(mapper, record, example);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public int updateByPrimaryKeySelective(Entity record) {
        try {
            mapper = (Mapper) getMapper();
            Method updateByPrimaryKeySelective = mapper.getClass().getDeclaredMethod("updateByPrimaryKeySelective", record.getClass());
            Object result = updateByPrimaryKeySelective.invoke(mapper, record);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }

        return 0;
    }

    @Override
    public int updateByPrimaryKeyWithBLOBs(Entity record) {
        try {
            mapper = (Mapper) getMapper();
            Method updateByPrimaryKeyWithBLOBs = mapper.getClass().getDeclaredMethod("updateByPrimaryKeyWithBLOBs", record.getClass());
            Object result = updateByPrimaryKeyWithBLOBs.invoke(mapper, record);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int updateByPrimaryKey(Entity record) {
        try {
            mapper = (Mapper) getMapper();
            Method updateByPrimaryKey = mapper.getClass().getDeclaredMethod("updateByPrimaryKey", record.getClass());
            Object result = updateByPrimaryKey.invoke(mapper, record);
            return Integer.parseInt(String.valueOf(result));
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int deleteByPrimaryKeys(String ids) {
        try {
            if (StringUtils.isBlank(ids)) {
                return 0;
            }
            String[] idArray = ids.split("-");
            int count = 0;
            for (String idStr : idArray) {
                if (StringUtils.isBlank(idStr)) {
                    continue;
                }
                Integer id = Integer.parseInt(idStr);
                mapper = (Mapper) getMapper();
                Method deleteByPrimaryKey = mapper.getClass().getDeclaredMethod("deleteByPrimaryKey", id.getClass());
                Object result = deleteByPrimaryKey.invoke(mapper, id);
                count += Integer.parseInt(String.valueOf(result));
            }
            return count;
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    /**
     * @return
     */
    public Object getMapper() {
        Class cls = (Class) ((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0];
        return ApplicationContextUtil.getBean((Class<Object>) cls);
    }

    /**
     * @param example
     * @param obj
     * @return
     */
    private List<Entity> getEntities(Example example, List<Entity> obj) {
        try {
            Method method = mapper.getClass().getDeclaredMethod("selectByExample", example.getClass());
            obj = (List<Entity>) method.invoke(mapper, example);
        } catch (NoSuchMethodException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return obj;
    }
}
