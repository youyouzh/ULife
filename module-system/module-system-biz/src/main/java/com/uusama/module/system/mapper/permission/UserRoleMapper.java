package com.uusama.module.system.mapper.permission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uusama.framework.mybatis.mapper.BaseMapperX;
import com.uusama.module.system.entity.permission.UserRoleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapperX<UserRoleDO> {

    default List<UserRoleDO> selectListByUserId(Long userId) {
        return selectList(UserRoleDO::getUserId, userId);
    }

    default void deleteListByUserIdAndRoleIdIds(Long userId, Collection<Long> roleIds) {
        delete(new LambdaQueryWrapper<UserRoleDO>()
                .eq(UserRoleDO::getUserId, userId)
                .in(UserRoleDO::getRoleId, roleIds));
    }

    default void deleteListByUserId(Long userId) {
        delete(new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId));
    }

    default void deleteListByRoleId(Long roleId) {
        delete(new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getRoleId, roleId));
    }

    default List<UserRoleDO> selectListByRoleIds(Collection<Long> roleIds) {
        return selectList(UserRoleDO::getRoleId, roleIds);
    }

}
