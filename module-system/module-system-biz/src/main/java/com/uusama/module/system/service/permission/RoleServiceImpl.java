package com.uusama.module.system.service.permission;

import com.google.common.annotations.VisibleForTesting;
import com.uusama.common.util.CollUtil;
import com.uusama.common.util.ObjectUtil;
import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleExportReqVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleUpdateReqVO;
import com.uusama.module.system.convert.permission.RoleConvert;
import com.uusama.module.system.entity.permission.RoleDO;
import com.uusama.module.system.enums.DataScopeEnum;
import com.uusama.module.system.enums.RoleCodeEnum;
import com.uusama.module.system.enums.RoleTypeEnum;
import com.uusama.module.system.mapper.permission.RoleMapper;
import com.uusama.module.system.mapper.permission.RoleMenuMapper;
import com.uusama.module.system.mapper.permission.UserRoleMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.uusama.framework.web.exception.ServiceExceptionUtil.exception;
import static com.uusama.module.system.constant.ErrorCodeConstants.ROLE_ADMIN_CODE_ERROR;
import static com.uusama.module.system.constant.ErrorCodeConstants.ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE;
import static com.uusama.module.system.constant.ErrorCodeConstants.ROLE_CODE_DUPLICATE;
import static com.uusama.module.system.constant.ErrorCodeConstants.ROLE_IS_DISABLE;
import static com.uusama.module.system.constant.ErrorCodeConstants.ROLE_NAME_DUPLICATE;
import static com.uusama.module.system.constant.ErrorCodeConstants.ROLE_NOT_EXISTS;

/**
 * 角色 Service 实现类
 *
 * @author uusama
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    /**
     * 角色缓存
     * key：角色编号 {@link RoleDO#getId()}
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    private volatile Map<Long, RoleDO> roleCache;

    private final RoleMapper roleMapper;
    private final RoleMenuMapper  roleMenuMapper;
    private final UserRoleMapper userRoleMapper;

    /**
     * 初始化 {@link #roleCache} 缓存
     */
    @Override
    @PostConstruct
    public void initLocalCache() {
        // 注意：忽略自动多租户，因为要全局初始化缓存
        // 第一步：查询数据
        List<RoleDO> roleList = roleMapper.selectList();
        log.info("[initLocalCache][缓存角色，数量为:{}]", roleList.size());

        // 第二步：构建缓存
        roleCache = CollUtil.convertMap(roleList, RoleDO::getId);
    }

    @Override
    @Transactional
    public Long createRole(RoleCreateReqVO reqVO, RoleTypeEnum type) {
        // 校验角色
        validateRoleDuplicate(reqVO.getName(), reqVO.getCode(), null);
        // 插入到数据库
        RoleDO role = RoleConvert.INSTANCE.convert(reqVO);
        role.setType(ObjectUtil.defaultIfNull(type, RoleTypeEnum.CUSTOM));
        role.setState(CommonState.ENABLE);
        role.setDataScope(DataScopeEnum.ALL); // 默认可查看所有数据。原因是，可能一些项目不需要项目权限
        roleMapper.insert(role);
        // 返回
        return role.getId();
    }

    @Override
    public void updateRole(RoleUpdateReqVO reqVO) {
        // 校验是否可以更新
        validateRoleForUpdate(reqVO.getId());
        // 校验角色的唯一字段是否重复
        validateRoleDuplicate(reqVO.getName(), reqVO.getCode(), reqVO.getId());

        // 更新到数据库
        RoleDO updateObj = RoleConvert.INSTANCE.convert(reqVO);
        roleMapper.updateById(updateObj);
    }

    @Override
    public void updateRoleStatus(Long id, CommonState state) {
        // 校验是否可以更新
        validateRoleForUpdate(id);

        // 更新状态
        RoleDO updateObj = RoleDO.builder().id(id).state(state).build();
        roleMapper.updateById(updateObj);
    }

    @Override
    public void updateRoleDataScope(Long id, DataScopeEnum dataScope, Set<Long> dataScopeDeptIds) {
        // 校验是否可以更新
        validateRoleForUpdate(id);

        // 更新数据范围
        RoleDO updateObject = RoleDO.builder().id(id).dataScope(dataScope)
            .dataScopeDeptIds(dataScopeDeptIds).build();
        roleMapper.updateById(updateObject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        // 校验是否可以更新
        validateRoleForUpdate(id);
        // 标记删除
        roleMapper.deleteById(id);
        // 删除相关数据
        // 标记删除 UserRole
        userRoleMapper.deleteListByRoleId(id);
        // 标记删除 RoleMenu
        roleMenuMapper.deleteListByRoleId(id);
    }

    @Override
    public RoleDO getRoleFromCache(Long id) {
        return roleCache.get(id);
    }

    @Override
    public List<RoleDO> getRoleListByStatus(@Nullable Collection<CommonState> states) {
        if (CollUtil.isEmpty(states)) {
    		return roleMapper.selectList();
		}
        return roleMapper.selectListByStatus(states);
    }

    @Override
    public List<RoleDO> getRoleListFromCache(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return roleCache.values().stream().filter(roleDO -> ids.contains(roleDO.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasAnySuperAdmin(Collection<RoleDO> roleList) {
        if (CollUtil.isEmpty(roleList)) {
            return false;
        }
        return roleList.stream().anyMatch(role -> RoleCodeEnum.isSuperAdmin(role.getCode()));
    }

    @Override
    public RoleDO getRole(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public PageResult<RoleDO> getRolePage(RolePageReqVO reqVO) {
        return roleMapper.selectPage(reqVO);
    }

    @Override
    public List<RoleDO> getRoleList(RoleExportReqVO reqVO) {
        return roleMapper.selectList(reqVO);
    }

    /**
     * 校验角色的唯一字段是否重复
     *
     * 1. 是否存在相同名字的角色
     * 2. 是否存在相同编码的角色
     *
     * @param name 角色名字
     * @param code 角色额编码
     * @param id 角色编号
     */
    @VisibleForTesting
    void validateRoleDuplicate(String name, String code, Long id) {
        // 0. 超级管理员，不允许创建
        if (RoleCodeEnum.isSuperAdmin(code)) {
            throw exception(ROLE_ADMIN_CODE_ERROR, code);
        }
        // 1. 该 name 名字被其它角色所使用
        RoleDO role = roleMapper.selectByName(name);
        if (role != null && !role.getId().equals(id)) {
            throw exception(ROLE_NAME_DUPLICATE, name);
        }
        // 2. 是否存在相同编码的角色
        if (!StringUtils.hasText(code)) {
            return;
        }
        // 该 code 编码被其它角色所使用
        role = roleMapper.selectByCode(code);
        if (role != null && !role.getId().equals(id)) {
            throw exception(ROLE_CODE_DUPLICATE, code);
        }
    }

    /**
     * 校验角色是否可以被更新
     *
     * @param id 角色编号
     */
    @VisibleForTesting
    void validateRoleForUpdate(Long id) {
        RoleDO roleDO = roleMapper.selectById(id);
        if (roleDO == null) {
            throw exception(ROLE_NOT_EXISTS);
        }
        // 内置角色，不允许删除
        if (RoleTypeEnum.SYSTEM.equals(roleDO.getType())) {
            throw exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
        }
    }

    @Override
    public void validateRoleList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得角色信息
        List<RoleDO> roles = roleMapper.selectBatchIds(ids);
        Map<Long, RoleDO> roleMap = CollUtil.convertMap(roles, RoleDO::getId);
        // 校验
        ids.forEach(id -> {
            RoleDO role = roleMap.get(id);
            if (role == null) {
                throw exception(ROLE_NOT_EXISTS);
            }
            if (role.getState().isDisable()) {
                throw exception(ROLE_IS_DISABLE, role.getName());
            }
        });
    }
}
