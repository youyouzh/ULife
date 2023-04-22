package com.uusama.module.system.service.permission;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.uusama.common.util.CollUtil;
import com.uusama.common.util.StrUtil;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.module.system.controller.admin.permission.vo.menu.MenuCreateReqVO;
import com.uusama.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.uusama.module.system.controller.admin.permission.vo.menu.MenuUpdateReqVO;
import com.uusama.module.system.convert.permission.MenuConvert;
import com.uusama.module.system.entity.permission.MenuDO;
import com.uusama.module.system.enums.MenuTypeEnum;
import com.uusama.module.system.mapper.permission.MenuMapper;
import com.uusama.module.system.mapper.permission.RoleMenuMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.uusama.framework.web.exception.ServiceExceptionUtil.exception;
import static com.uusama.module.system.constant.ErrorCodeConstants.MENU_EXISTS_CHILDREN;
import static com.uusama.module.system.constant.ErrorCodeConstants.MENU_NAME_DUPLICATE;
import static com.uusama.module.system.constant.ErrorCodeConstants.MENU_NOT_EXISTS;
import static com.uusama.module.system.constant.ErrorCodeConstants.MENU_PARENT_ERROR;
import static com.uusama.module.system.constant.ErrorCodeConstants.MENU_PARENT_NOT_DIR_OR_MENU;
import static com.uusama.module.system.constant.ErrorCodeConstants.MENU_PARENT_NOT_EXISTS;
import static com.uusama.module.system.entity.permission.MenuDO.ID_ROOT;

/**
 * 菜单 Service 实现
 *
 * @author uusama
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    /**
     * 菜单缓存
     * key：菜单编号
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    @Setter
    private volatile Map<Long, MenuDO> menuCache;
    /**
     * 权限与菜单缓存
     * key：权限 {@link MenuDO#getPermission()}
     * value：MenuDO 数组，因为一个权限可能对应多个 MenuDO 对象
     *
     * 这里声明 volatile 修饰的原因是，每次刷新时，直接修改指向
     */
    @Getter
    @Setter
    private volatile Multimap<String, MenuDO> permissionMenuCache;

    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;

    /**
     * 初始化 {@link #menuCache} 和 {@link #permissionMenuCache} 缓存
     */
    @Override
    @PostConstruct
    public synchronized void initLocalCache() {
        // 第一步：查询数据
        List<MenuDO> menuList = menuMapper.selectList();
        log.info("[initLocalCache][缓存菜单，数量为:{}]", menuList.size());

        // 第二步：构建缓存
        ImmutableMap.Builder<Long, MenuDO> menuCacheBuilder = ImmutableMap.builder();
        ImmutableMultimap.Builder<String, MenuDO> permMenuCacheBuilder = ImmutableMultimap.builder();
        menuList.forEach(menuDO -> {
            menuCacheBuilder.put(menuDO.getId(), menuDO);
            if (StrUtil.isNotEmpty(menuDO.getPermission())) { // 会存在 permission 为 null 的情况，导致 put 报 NPE 异常
                permMenuCacheBuilder.put(menuDO.getPermission(), menuDO);
            }
        });
        menuCache = menuCacheBuilder.build();
        permissionMenuCache = permMenuCacheBuilder.build();
    }

    @Override
    public Long createMenu(MenuCreateReqVO reqVO) {
        // 校验父菜单存在
        validateParentMenu(reqVO.getParentId(), null);
        // 校验菜单（自己）
        validateMenu(reqVO.getParentId(), reqVO.getName(), null);

        // 插入数据库
        MenuDO menu = MenuConvert.INSTANCE.convert(reqVO);
        initMenuProperty(menu);
        menuMapper.insert(menu);
        // 返回
        return menu.getId();
    }

    @Override
    public void updateMenu(MenuUpdateReqVO reqVO) {
        // 校验更新的菜单是否存在
        if (menuMapper.selectById(reqVO.getId()) == null) {
            throw exception(MENU_NOT_EXISTS);
        }
        // 校验父菜单存在
        validateParentMenu(reqVO.getParentId(), reqVO.getId());
        // 校验菜单（自己）
        validateMenu(reqVO.getParentId(), reqVO.getName(), reqVO.getId());

        // 更新到数据库
        MenuDO updateObject = MenuConvert.INSTANCE.convert(reqVO);
        initMenuProperty(updateObject);
        menuMapper.updateById(updateObject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long menuId) {
        // 校验是否还有子菜单
        if (menuMapper.selectCountByParentId(menuId) > 0) {
            throw exception(MENU_EXISTS_CHILDREN);
        }
        // 校验删除的菜单是否存在
        if (menuMapper.selectById(menuId) == null) {
            throw exception(MENU_NOT_EXISTS);
        }
        // 标记删除
        menuMapper.deleteById(menuId);
        // 删除授予给角色的权限
        roleMenuMapper.deleteListByMenuId(menuId);
    }

    @Override
    public List<MenuDO> getMenuList() {
        return menuMapper.selectList();
    }

    @Override
    public List<MenuDO> getMenuListByTenant(MenuListReqVO reqVO) {
        return getMenuList(reqVO);
    }

    @Override
    public List<MenuDO> getMenuList(MenuListReqVO reqVO) {
        return menuMapper.selectList(reqVO);
    }

    @Override
    public List<MenuDO> getMenuListFromCache(Collection<MenuTypeEnum> menuTypes, Collection<CommonState> menuStates) {
        // 任一一个参数为空，则返回空
        if (CollUtil.isAnyEmpty(menuTypes, menuStates)) {
            return Collections.emptyList();
        }
        // 创建新数组，避免缓存被修改
        return menuCache.values().stream().filter(menu -> menuTypes.contains(menu.getType())
                && menuStates.contains(menu.getState()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuDO> getMenuListFromCache(Collection<Long> menuIds, Collection<MenuTypeEnum> menuTypes,
                                             Collection<CommonState> menuStates) {
        // 任一一个参数为空，则返回空
        if (CollUtil.isAnyEmpty(menuIds, menuTypes, menuStates)) {
            return Collections.emptyList();
        }
        return menuCache.values().stream().filter(menu -> menuIds.contains(menu.getId())
                && menuTypes.contains(menu.getType())
                && menuStates.contains(menu.getState()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuDO> getMenuListByPermissionFromCache(String permission) {
        return new ArrayList<>(permissionMenuCache.get(permission));
    }

    @Override
    public MenuDO getMenu(Long id) {
        return menuMapper.selectById(id);
    }

    /**
     * 校验父菜单是否合法
     *
     * 1. 不能设置自己为父菜单
     * 2. 父菜单不存在
     * 3. 父菜单必须是 {@link MenuTypeEnum#MENU} 菜单类型
     *
     * @param parentId 父菜单编号
     * @param childId 当前菜单编号
     */
    @VisibleForTesting
    void validateParentMenu(Long parentId, Long childId) {
        if (parentId == null || ID_ROOT.equals(parentId)) {
            return;
        }
        // 不能设置自己为父菜单
        if (parentId.equals(childId)) {
            throw exception(MENU_PARENT_ERROR);
        }
        MenuDO menu = menuMapper.selectById(parentId);
        // 父菜单不存在
        if (menu == null) {
            throw exception(MENU_PARENT_NOT_EXISTS);
        }
        // 父菜单必须是目录或者菜单类型
        if (!MenuTypeEnum.DIR.equals(menu.getType())
            && !MenuTypeEnum.MENU.equals(menu.getType())) {
            throw exception(MENU_PARENT_NOT_DIR_OR_MENU);
        }
    }

    /**
     * 校验菜单是否合法
     *
     * 1. 校验相同父菜单编号下，是否存在相同的菜单名
     *
     * @param name 菜单名字
     * @param parentId 父菜单编号
     * @param id 菜单编号
     */
    @VisibleForTesting
    void validateMenu(Long parentId, String name, Long id) {
        MenuDO menu = menuMapper.selectByParentIdAndName(parentId, name);
        if (menu == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的菜单
        if (id == null) {
            throw exception(MENU_NAME_DUPLICATE);
        }
        if (!menu.getId().equals(id)) {
            throw exception(MENU_NAME_DUPLICATE);
        }
    }

    /**
     * 初始化菜单的通用属性。
     *
     * 例如说，只有目录或者菜单类型的菜单，才设置 icon
     *
     * @param menu 菜单
     */
    private void initMenuProperty(MenuDO menu) {
        // 菜单为按钮类型时，无需 component、icon、path 属性，进行置空
        if (MenuTypeEnum.BUTTON.equals(menu.getType())) {
            menu.setComponent("");
            menu.setComponentName("");
            menu.setIcon("");
            menu.setPath("");
        }
    }

}
