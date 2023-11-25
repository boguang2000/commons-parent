package cn.aotcloud.mybatis.plus;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    /**
     * 批量插入 仅适用于mysql
     * @param entityList 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(List<T> entityList);
    
    /**
     * 自定义批量新增或更新
     * 需要配合自定注解 DuplicateSql 使用
     * @param list
     * @return
     */
    Boolean insertOrUpdateBath(@Param("list") List<T> list);

}
