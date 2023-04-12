package com.uusama.module.system.service.permission;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.uusama.common.util.ArrayUtil;
import com.uusama.common.util.CollUtil;
import com.uusama.framework.security.api.PermissionApi;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.framework.web.util.JsonUtils;
import com.uusama.module.system.entity.dept.DeptDO;
import com.uusama.module.system.entity.permission.MenuDO;
import com.uusama.module.system.entity.permission.RoleDO;
import com.uusama.module.system.entity.permission.RoleMenuDO;
import com.uusama.module.system.entity.permission.UserRoleDO;
import com.uusama.module.system.enums.DataScopeEnum;
import com.uusama.module.system.enums.MenuTypeEnum;
import com.uusama.module.system.mapper.permission.RoleMenuMapper;
import com.uusama.module.system.mapper.permission.UserRoleMapper;
import com.uusama.module.system.mapper.user.AdminUserMapper;
import com.uusama.module.system.service.dept.DeptService;
import com.uusama.module.system.service.permission.bo.DeptDataPermissionRespDTO;
import com.uusama.module.system.service.user.AdminUserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

/**
 * 权限 Service 实现类
 *
 * @author uusama
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService, PermissionApi {

    /**
     * 角色编号与菜单编号的缓存映射
     * key：角色编号
     * value：菜单编号的数组
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    @Setter // 单元测试需要
    private volatile Multimap<Long, Long> roleMenuCache;
    /**
     * 菜单编号与角色编号的缓存映射
     * key：菜单编号
     * value：角色编号的数组
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    @Setter // 单元测试需要
    private volatile Multimap<Long, Long> menuRoleCache;

    /**
     * 用户编号与角色编号的缓存映射
     * key：用户编号
     * value：角色编号的数组
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    @Setter // 单元测试需要
    private volatile Map<Long, Set<Long>> userRoleCache;

    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;
    private final AdminUserMapper adminUserMapper;
    private final RoleService roleService;
    private final MenuService menuService;
    private final DeptService deptService;

    @Override
    @PostConstruct
    public void initLocalCache() {
        initLocalCacheForRoleMenu();
        initLocalCacheForUserRole();
    }

    /**
     * 刷新 RoleMenu 本地缓存
     */
    @VisibleForTesting
    void initLocalCacheForRoleMenu() {
        // 注意：忽略自动多租户，因为要全局初始化缓存
            // 第一步：查询数据
        List<RoleMenuDO> roleMenus = roleMenuMapper.selectList();
        log.info("[initLocalCacheForRoleMenu][缓存角色与菜单，数量为:{}]", roleMenus.size());

        // 第二步：构建缓存
        ImmutableMultimap.Builder<Long, Long> roleMenuCacheBuilder = ImmutableMultimap.builder();
        ImmutableMultimap.Builder<Long, Long> menuRoleCacheBuilder = ImmutableMultimap.builder();
        roleMenus.forEach(roleMenuDO -> {
            roleMenuCacheBuilder.put(roleMenuDO.getRoleId(), roleMenuDO.getMenuId());
            menuRoleCacheBuilder.put(roleMenuDO.getMenuId(), roleMenuDO.getRoleId());
        });
        roleMenuCache = roleMenuCacheBuilder.build();
        menuRoleCache = menuRoleCacheBuilder.build();
    }

    /**
     * 刷新 UserRole 本地缓存
     */
    @VisibleForTesting
    void initLocalCacheForUserRole() {
        // 注意：忽略自动多租户，因为要全局初始化缓存
        // 第一步：加载数据
        List<UserRoleDO> userRoles = userRoleMapper.selectList();
        log.info("[initLocalCacheForUserRole][缓存用户与角色，数量为:{}]", userRoles.size());

        // 第二步：构建缓存。
        ImmutableMultimap.Builder<Long, Long> userRoleCacheBuilder = ImmutableMultimap.builder();
        userRoles.forEach(userRoleDO -> userRoleCacheBuilder.put(userRoleDO.getUserId(), userRoleDO.getRoleId()));
        userRoleCache = CollUtil.convertMultiMap2(userRoles, UserRoleDO::getUserId, UserRoleDO::getRoleId);
    }

    @Override
    public List<MenuDO> getRoleMenuListFromCache(Collection<Long> roleIds, Collection<MenuTypeEnum> menuTypes,
                                                 Collection<CommonState> menusStates) {
        // 任一一个参数为空时，不返回任何菜单
        if (CollUtil.isAnyEmpty(roleIds, menuTypes, menusStates)) {
            return Collections.emptyList();
        }

        // 判断角色是否包含超级管理员。如果是超级管理员，获取到全部
        List<RoleDO> roleList = roleService.getRoleListFromCache(roleIds);
        if (roleService.hasAnySuperAdmin(roleList)) {
            return menuService.getMenuListFromCache(menuTypes, menusStates);
        }

        // 获得角色拥有的菜单关联
        List<Long> menuIds = roleMenuCache.entries().stream()
            .filter(v -> roleIds.contains(v.getKey()))
            .map(Map.Entry::getValue).collect(Collectors.toList());
        return menuService.getMenuListFromCache(menuIds, menuTypes, menusStates);
    }

    @Override
    public Set<Long> getUserRoleIdsFromCache(Long userId, Collection<CommonState> roleStates) {
        Set<Long> cacheRoleIds = userRoleCache.get(userId);
        // 创建用户的时候没有分配角色，会存在空指针异常
        if (CollUtil.isEmpty(cacheRoleIds)) {
            return Collections.emptySet();
        }
        Set<Long> roleIds = new HashSet<>(cacheRoleIds);
        // 过滤角色状态
        if (CollUtil.isNotEmpty(roleStates)) {
            roleIds.removeIf(roleId -> {
                RoleDO role = roleService.getRoleFromCache(roleId);
                return role == null || !roleStates.contains(role.getState());
            });
        }
        return roleIds;
    }

    @Override
    public Set<Long> getRoleMenuIds(Long roleId) {
        // 如果是管理员的情况下，获取全部菜单编号
        if (roleService.hasAnySuperAdmin(Collections.singleton(roleId))) {
            return CollUtil.convertSet(menuService.getMenuList(), MenuDO::getId);
        }
        // 如果是非管理员的情况下，获得拥有的菜单编号
        return CollUtil.convertSet(roleMenuMapper.selectListByRoleId(roleId), RoleMenuDO::getMenuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenu(Long roleId, Set<Long> menuIds) {
        // 获得角色拥有菜单编号
        Set<Long> dbMenuIds = CollUtil.convertSet(roleMenuMapper.selectListByRoleId(roleId),
                RoleMenuDO::getMenuId);
        // 计算新增和删除的菜单编号
        Collection<Long> createMenuIds = CollectionUtils.subtract(menuIds, dbMenuIds);
        Collection<Long> deleteMenuIds = CollectionUtils.subtract(dbMenuIds, menuIds);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (!CollUtil.isEmpty(createMenuIds)) {
            roleMenuMapper.insertBatch(CollUtil.convertList(createMenuIds, menuId -> {
                RoleMenuDO entity = new RoleMenuDO();
                entity.setRoleId(roleId);
                entity.setMenuId(menuId);
                return entity;
            }));
        }
        if (!CollUtil.isEmpty(deleteMenuIds)) {
            roleMenuMapper.deleteListByRoleIdAndMenuIds(roleId, deleteMenuIds);
        }
    }

    @Override
    public Set<Long> getUserRoleIdListByUserId(Long userId) {
        return CollUtil.convertSet(userRoleMapper.selectListByUserId(userId),
                UserRoleDO::getRoleId);
    }

    @Override
    public Set<Long> getUserRoleIdListByRoleIds(Collection<Long> roleIds) {
        return CollUtil.convertSet(userRoleMapper.selectListByRoleIds(roleIds),
                UserRoleDO::getUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRole(Long userId, Set<Long> roleIds) {
        // 获得角色拥有角色编号
        Set<Long> dbRoleIds = CollUtil.convertSet(userRoleMapper.selectListByUserId(userId),
                UserRoleDO::getRoleId);
        // 计算新增和删除的角色编号
        Collection<Long> createRoleIds = CollectionUtils.subtract(roleIds, dbRoleIds);
        Collection<Long> deleteMenuIds = CollectionUtils.subtract(dbRoleIds, roleIds);
        // 执行新增和删除。对于已经授权的角色，不用做任何处理
        if (!CollUtil.isEmpty(createRoleIds)) {
            userRoleMapper.insertBatch(CollUtil.convertList(createRoleIds, roleId -> {
                UserRoleDO entity = new UserRoleDO();
                entity.setUserId(userId);
                entity.setRoleId(roleId);
                return entity;
            }));
        }
        if (!CollUtil.isEmpty(deleteMenuIds)) {
            userRoleMapper.deleteListByUserIdAndRoleIdIds(userId, deleteMenuIds);
        }
    }

    @Override
    public void assignRoleDataScope(Long roleId, DataScopeEnum dataScope, Set<Long> dataScopeDeptIds) {
        roleService.updateRoleDataScope(roleId, dataScope, dataScopeDeptIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processRoleDeleted(Long roleId) {
        // 标记删除 UserRole
        userRoleMapper.deleteListByRoleId(roleId);
        // 标记删除 RoleMenu
        roleMenuMapper.deleteListByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processMenuDeleted(Long menuId) {
        roleMenuMapper.deleteListByMenuId(menuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processUserDeleted(Long userId) {
        userRoleMapper.deleteListByUserId(userId);
    }

    @Override
    public boolean hasAnyPermissions(Long userId, String... permissions) {
        // 如果为空，说明已经有权限
        if (ArrayUtil.isEmpty(permissions)) {
            return true;
        }

        // 获得当前登录的角色。如果为空，说明没有权限
        Set<Long> roleIds = getUserRoleIdsFromCache(userId, singleton(CommonState.ENABLE));
        if (CollUtil.isEmpty(roleIds)) {
            return false;
        }
        // 判断是否是超管。如果是，当然符合条件
        if (roleService.hasAnySuperAdmin(roleIds)) {
            return true;
        }

        // 遍历权限，判断是否有一个满足
        return Arrays.stream(permissions).anyMatch(permission -> {
            List<MenuDO> menuList = menuService.getMenuListByPermissionFromCache(permission);
            // 采用严格模式，如果权限找不到对应的 Menu 的话，认为
            if (CollUtil.isEmpty(menuList)) {
                return false;
            }
            // 获得是否拥有该权限，任一一个
            return menuList.stream().anyMatch(menu -> CollUtil.containsAny(roleIds,
                    menuRoleCache.get(menu.getId())));
        });
    }

    @Override
    public boolean hasAnyRoles(Long userId, String... roles) {
        // 如果为空，说明已经有权限
        if (ArrayUtil.isEmpty(roles)) {
            return true;
        }

        // 获得当前登录的角色。如果为空，说明没有权限
        Set<Long> roleIds = getUserRoleIdsFromCache(userId, singleton(CommonState.ENABLE));
        if (CollUtil.isEmpty(roleIds)) {
            return false;
        }
        // 判断是否是超管。如果是，当然符合条件
        if (roleService.hasAnySuperAdmin(roleIds)) {
            return true;
        }
        Set<String> userRoles = CollUtil.convertSet(roleService.getRoleListFromCache(roleIds),
                RoleDO::getCode);
        return CollUtil.containsAny(userRoles, Sets.newHashSet(roles));
    }

    @Override
    // @DataPermission(enable = false) // 关闭数据权限，不然就会出现递归获取数据权限的问题
    public DeptDataPermissionRespDTO getDeptDataPermission(Long userId) {
        // 获得用户的角色
        Set<Long> roleIds = getUserRoleIdsFromCache(userId, singleton(CommonState.ENABLE));
        // 如果角色为空，则只能查看自己
        DeptDataPermissionRespDTO result = new DeptDataPermissionRespDTO();
        if (CollUtil.isEmpty(roleIds)) {
            result.setSelf(true);
            return result;
        }
        List<RoleDO> roles = roleService.getRoleListFromCache(roleIds);

        // 获得用户的部门编号的缓存，通过 Guava 的 Suppliers 惰性求值，即有且仅有第一次发起 DB 的查询
        Supplier<Long> userDeptIdCache = Suppliers.memoize(() -> adminUserMapper.selectById(userId).getDeptId());
        // 遍历每个角色，计算
        for (RoleDO role : roles) {
            // 为空时，跳过
            if (role.getDataScope() == null) {
                continue;
            }
            // 情况一，ALL
            if (role.getDataScope() == DataScopeEnum.ALL) {
                result.setAll(true);
                continue;
            }
            // 情况二，DEPT_CUSTOM
            if (role.getDataScope() == DataScopeEnum.DEPT_CUSTOM) {
                CollectionUtils.addAll(result.getDeptIds(), role.getDataScopeDeptIds());
                // 自定义可见部门时，保证可以看到自己所在的部门。否则，一些场景下可能会有问题。
                // 例如说，登录时，基于 t_user 的 username 查询会可能被 dept_id 过滤掉
                CollectionUtils.addAll(result.getDeptIds(), userDeptIdCache.get());
                continue;
            }
            // 情况三，DEPT_ONLY
            if (role.getDataScope() == DataScopeEnum.DEPT_ONLY) {
                if (userDeptIdCache.get() != null) {
                    CollectionUtils.addAll(result.getDeptIds(), userDeptIdCache.get());
                }
                continue;
            }
            // 情况四，DEPT_DEPT_AND_CHILD
            if (role.getDataScope() == DataScopeEnum.DEPT_AND_CHILD) {
                List<DeptDO> depts = deptService.getDeptListByParentIdFromCache(userDeptIdCache.get(), true);
                CollectionUtils.addAll(result.getDeptIds(), CollUtil.convertList(depts, DeptDO::getId));
                // 添加本身部门编号
                CollectionUtils.addAll(result.getDeptIds(), userDeptIdCache.get());
                continue;
            }
            // 情况五，SELF
            if (role.getDataScope() == DataScopeEnum.SELF) {
                result.setSelf(true);
                continue;
            }
            // 未知情况，error log 即可
            log.error("[getDeptDataPermission][LoginUser({}) role({}) 无法处理]", userId, JsonUtils.toJsonString(result));
        }
        return result;
    }

}
