package com.uusama.module.monitor.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.uusama.framework.mybatis.mapper.BaseMapperX;
import com.uusama.module.monitor.entity.BaseDO;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhaohai
 */
public interface BaseMapper<T extends BaseDO> extends BaseMapperX<T> {

    /**
     * 根据id批量查询数据，并做map映射
     * @param ids id列表
     * @return id -> entity map
     */
    default Map<Integer, T> selectMapByIds(Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new HashMap<>(0);
        }
        return selectBatchIds(ids).stream()
            .collect(Collectors.toMap(BaseDO::getId, v -> v));
    }

    /**
     * 获取或创建一条记录
     * @param baseEntity 基准实体
     * @return entity
     */
    default T getOrCreate(T baseEntity) {
        return getUnique(baseEntity).orElseGet(() -> {
            insert(baseEntity);
            return baseEntity;
        });
    }

    /**
     * 获取唯一值
     * @param baseEntity 基准实体
     * @return 唯一值，如果存在则为空
     */
    default Optional<T> getUnique(T baseEntity) {
        if (Objects.nonNull(baseEntity.getId())) {
            return Optional.of(selectById(baseEntity.getId()));
        }

        // 获取Unique查询值
        T existRecord = selectOne((Wrapper<T>) baseEntity.getUniqueQuery());
        if (Objects.nonNull(existRecord)) {
            return Optional.of(existRecord);
        }
        return Optional.empty();
    }

    /**
     * 是否存在逻辑上的唯一值，用于判断是否新建
     * @param baseEntity 基准实体
     * @return true表示已经存在
     */
    default boolean existUnique(T baseEntity) {
        return (Objects.nonNull(baseEntity.getId()) && Objects.nonNull(selectById(baseEntity.getId())))
            || Objects.nonNull(selectOne((Wrapper<T>) baseEntity.getUniqueQuery()));
    }

}
