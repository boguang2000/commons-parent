package cn.aotcloud.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;

import cn.aotcloud.mybatis.plus.BaseMapper;

public class BaseService<T> {

	private BaseMapper<T> baseMapper;
	
	public BaseService(BaseMapper<T> baseMapper) {
		this.baseMapper = baseMapper;
	}
	
	/**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
	public int insert(T entity) {
		return this.baseMapper.insert(entity);
	}

	/**
     * 批量插入记录
     *
     * @param entityList 实体对象列表
     */
	public int insertBatchSomeColumn(List<T> entityList) {
		return this.baseMapper.insertBatchSomeColumn(entityList);
	}
	
    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
	public int deleteById(Serializable id) {
		return this.baseMapper.deleteById(id);
	}

    /**
     * 根据实体(ID)删除
     *
     * @param entity 实体对象
     * @since 3.4.4
     */
	public int deleteById(T entity) {
		return this.baseMapper.deleteById(entity);
	}

    /**
     * 根据 columnMap 条件，删除记录
     *
     * @param columnMap 表字段 map 对象
     */
	public int deleteByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap) {
		return this.baseMapper.deleteByMap(columnMap);
	}

    /**
     * 根据 entity 条件，删除记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
	public int delete(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
		return this.baseMapper.delete(queryWrapper);
	}

    /**
     * 删除（根据ID或实体 批量删除）
     *
     * @param idList 主键ID列表或实体列表(不能为 null 以及 empty)
     */
	public int deleteBatchIds(@Param(Constants.COLL) Collection<?> idList) {
		return this.baseMapper.deleteBatchIds(idList);
	}

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
	public int updateById(@Param(Constants.ENTITY) T entity) {
		return this.baseMapper.updateById(entity);
	}

    /**
     * 根据 whereEntity 条件，更新记录
     *
     * @param entity        实体对象 (set 条件值,可以为 null)
     * @param updateWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
     */
	public int update(@Param(Constants.ENTITY) T entity, @Param(Constants.WRAPPER) Wrapper<T> updateWrapper) {
		return this.baseMapper.update(entity, updateWrapper);
	}

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
	public T selectById(Serializable id) {
		return this.baseMapper.selectById(id);
	}

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList 主键ID列表(不能为 null 以及 empty)
     */
	public List<T> selectBatchIds(@Param(Constants.COLL) Collection<? extends Serializable> idList) {
		return this.baseMapper.selectBatchIds(idList);
	}

    /**
     * 查询（根据 columnMap 条件）
     *
     * @param columnMap 表字段 map 对象
     */
	public  List<T> selectByMap(@Param(Constants.COLUMN_MAP) Map<String, Object> columnMap) {
		return this.baseMapper.selectByMap(columnMap);
	}

    /**
     * 根据 entity 条件，查询一条记录
     * <p>查询一条记录，例如 qw.last("limit 1") 限制取一条记录, 注意：多条数据会报异常</p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
	public T selectOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
        List<T> ts = this.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(ts)) {
            if (ts.size() != 1) {
                throw ExceptionUtils.mpe("One record is expected, but the query result is multiple records");
            }
            return ts.get(0);
        }
        return null;
    }

    /**
     * 根据 Wrapper 条件，判断是否存在记录
     *
     * @param queryWrapper 实体对象封装操作类
     * @return 是否存在记录
     */
	public boolean exists(Wrapper<T> queryWrapper) {
        Long count = this.selectCount(queryWrapper);
        return null != count && count > 0;
    }

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
	public Long selectCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
		return this.baseMapper.selectCount(queryWrapper);
	}

    /**
     * 根据 entity 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
	public List<T> selectList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
		return this.baseMapper.selectList(queryWrapper);
	}

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
	public List<Map<String, Object>> selectMaps(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
		return this.baseMapper.selectMaps(queryWrapper);
	}

    /**
     * 根据 Wrapper 条件，查询全部记录
     * <p>注意： 只返回第一个字段的值</p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
	public List<Object> selectObjs(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
		return this.baseMapper.selectObjs(queryWrapper);
	}

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
	public <P extends IPage<T>> P selectPage(P page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
		return this.baseMapper.selectPage(page, queryWrapper);
	}

    /**
     * 根据 Wrapper 条件，查询全部记录（并翻页）
     *
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     */
	public <P extends IPage<Map<String, Object>>> P selectMapsPage(P page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper) {
		return this.baseMapper.selectMapsPage(page, queryWrapper);
	}
}
