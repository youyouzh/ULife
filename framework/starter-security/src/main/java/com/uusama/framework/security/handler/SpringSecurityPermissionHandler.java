package com.uusama.framework.security.handler;

import com.uusama.framework.security.LoginUser;
import com.uusama.framework.security.api.PermissionApi;
import com.uusama.framework.security.util.SecurityFrameworkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;

import static com.uusama.framework.web.util.WebFrameworkUtils.getLoginUserId;

/**
 * @author uusama
 */
@RequiredArgsConstructor
public class SpringSecurityPermissionHandler {
    private final PermissionApi permissionApi;

    /**
     * 判断是否有权限
     *
     * @param permission 权限
     * @return 是否
     */
    public boolean hasPermission(String permission) {
        return hasAnyPermissions(permission);
    }

    /**
     * 判断是否有权限，任一一个即可
     *
     * @param permissions 权限
     * @return 是否
     */
    public boolean hasAnyPermissions(String... permissions) {
        return permissionApi.hasAnyPermissions(getLoginUserId(), permissions);
    }

    /**
     * 判断是否有角色
     * 注意，角色使用的是 SysRoleDO 的 code 标识
     *
     * @param role 角色
     * @return 是否
     */
    public boolean hasRole(String role) {
        return hasAnyRoles(role);
    }

    /**
     * 判断是否有角色，任一一个即可
     *
     * @param roles 角色数组
     * @return 是否
     */
    public boolean hasAnyRoles(String... roles) {
        return permissionApi.hasAnyRoles(getLoginUserId(), roles);
    }

    /**
     * 判断是否有授权
     *
     * @param scope 授权
     * @return 是否
     */
    public boolean hasScope(String scope) {
        return hasAnyScopes(scope);
    }

    /**
     * 判断是否有授权范围，任一一个即可
     *
     * @param scope 授权范围数组
     * @return 是否
     */
    public boolean hasAnyScopes(String... scope) {
        return SecurityFrameworkUtils.getLoginUser()
            .map(LoginUser::getScopes)
            .filter(v -> CollectionUtils.containsAny(v, Arrays.asList(scope)))
            .isPresent();
    }
}
