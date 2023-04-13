package com.uusama.module.system.controller.admin.auth;

import com.uusama.framework.recorder.annotations.OperateLog;
import com.uusama.framework.security.config.SecurityProperties;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.framework.web.pojo.CommonResult;
import com.uusama.framework.web.util.ParamUtils;
import com.uusama.module.system.constant.ErrorCodeConstants;
import com.uusama.module.system.controller.admin.auth.vo.AuthLoginReqVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthMenuRespVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthSocialLoginReqVO;
import com.uusama.module.system.convert.user.AuthConvert;
import com.uusama.module.system.entity.permission.MenuDO;
import com.uusama.module.system.entity.permission.RoleDO;
import com.uusama.module.system.entity.user.AdminUserDO;
import com.uusama.module.system.enums.MenuTypeEnum;
import com.uusama.module.system.logger.LoginLogTypeEnum;
import com.uusama.module.system.service.auth.AdminAuthService;
import com.uusama.module.system.service.permission.PermissionService;
import com.uusama.module.system.service.permission.RoleService;
import com.uusama.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.uusama.framework.security.util.SecurityFrameworkUtils.obtainAuthorization;
import static com.uusama.framework.web.util.WebFrameworkUtils.getLoginUserId;
import static java.util.Collections.singleton;

@Tag(name = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AdminAuthService authService;
    private final AdminUserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final SecurityProperties securityProperties;

    @PostMapping("/login")
    @PermitAll
    @Operation(summary = "使用账号密码登录")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        return CommonResult.success(authService.login(reqVO));
    }

    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "登出系统")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        Optional<String> token = obtainAuthorization(request, securityProperties.getTokenHeader());
        token.ifPresent(s -> authService.logout(s, LoginLogTypeEnum.LOGOUT_SELF));
        return CommonResult.success(true);
    }

    @PostMapping("/refresh-token")
    @PermitAll
    @Operation(summary = "刷新令牌")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<AuthLoginRespVO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return CommonResult.success(authService.refreshToken(refreshToken));
    }

    @GetMapping("/get-permission-info")
    @Operation(summary = "获取登录用户的权限信息")
    public CommonResult<AuthPermissionInfoRespVO> getPermissionInfo() {
        // 获得用户信息
        AdminUserDO user = userService.getUser(getLoginUserId());
        ParamUtils.checkNotNull(user, ErrorCodeConstants.USER_NOT_EXISTS);
        // 获得角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdsFromCache(getLoginUserId(), singleton(CommonState.ENABLE));
        List<RoleDO> roleList = roleService.getRoleListFromCache(roleIds);
        // 获得菜单列表
        List<MenuDO> menuList = permissionService.getRoleMenuListFromCache(roleIds,
                                                                           Arrays.asList(MenuTypeEnum.DIR, MenuTypeEnum.MENU, MenuTypeEnum.BUTTON),
                                                                           singleton(CommonState.ENABLE)); // 只要开启的
        // 拼接结果返回
        return CommonResult.success(AuthConvert.INSTANCE.convert(user, roleList, menuList));
    }

    @GetMapping("/list-menus")
    @Operation(summary = "获得登录用户的菜单列表")
    public CommonResult<List<AuthMenuRespVO>> getMenuList() {
        // 获得角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdsFromCache(getLoginUserId(), singleton(CommonState.ENABLE));
        // 获得用户拥有的目录和菜单类型
        List<MenuDO> menuList = permissionService.getRoleMenuListFromCache(roleIds,
                                                                           Arrays.asList(MenuTypeEnum.DIR, MenuTypeEnum.MENU),
                                                                           singleton(CommonState.ENABLE));
        // 转换成 Tree 结构返回
        return CommonResult.success(AuthConvert.INSTANCE.buildMenuTree(menuList));
    }

    // ========== 短信登录相关 ==========

    // ========== 社交登录相关 ==========
//
//    @GetMapping("/social-auth-redirect")
//    @PermitAll
//    @Operation(summary = "社交授权的跳转")
//    @Parameters({
//            @Parameter(name = "type", description = "社交类型", required = true),
//            @Parameter(name = "redirectUri", description = "回调路径")
//    })
//    public CommonResult<String> socialLogin(@RequestParam("type") Integer type,
//                                                    @RequestParam("redirectUri") String redirectUri) {
//        return CommonResult.success(socialUserService.getAuthorizeUrl(type, redirectUri));
//    }

    @PostMapping("/social-login")
    @PermitAll
    @Operation(summary = "社交快捷登录，使用 code 授权码", description = "适合未登录的用户，但是社交账号已绑定用户")
    @OperateLog(enable = false) // 避免 Post 请求被记录操作日志
    public CommonResult<AuthLoginRespVO> socialQuickLogin(@RequestBody @Valid AuthSocialLoginReqVO reqVO) {
        return CommonResult.success(authService.socialLogin(reqVO));
    }

}
