package com.uusama.module.system.convert.user;

import com.uusama.common.util.CollUtil;
import com.uusama.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.uusama.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.uusama.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthMenuRespVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthSmsLoginReqVO;
import com.uusama.module.system.controller.admin.auth.vo.AuthSmsSendReqVO;
import com.uusama.module.system.entity.oauth2.OAuth2AccessTokenDO;
import com.uusama.module.system.entity.permission.MenuDO;
import com.uusama.module.system.entity.permission.RoleDO;
import com.uusama.module.system.entity.user.AdminUserDO;
import com.uusama.module.system.enums.SmsSceneEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.uusama.module.system.entity.permission.MenuDO.ID_ROOT;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    AuthLoginRespVO convert(OAuth2AccessTokenDO bean);

    default AuthPermissionInfoRespVO convert(AdminUserDO user, List<RoleDO> roleList, List<MenuDO> menuList) {
        return AuthPermissionInfoRespVO.builder()
            .user(AuthPermissionInfoRespVO.UserVO.builder().id(user.getId()).nickname(user.getNickname()).avatar(user.getAvatar()).build())
            .roles(CollUtil.convertSet(roleList, RoleDO::getCode))
            .permissions(CollUtil.convertSet(menuList, MenuDO::getPermission))
            .build();
    }

    AuthMenuRespVO convertTreeNode(MenuDO menu);

    /**
     * 将菜单列表，构建成菜单树
     *
     * @param menuList 菜单列表
     * @return 菜单树
     */
    default List<AuthMenuRespVO> buildMenuTree(List<MenuDO> menuList) {
        // 排序，保证菜单的有序性
        menuList.sort(Comparator.comparing(MenuDO::getSort));
        // 构建菜单树
        // 使用 LinkedHashMap 的原因，是为了排序 。实际也可以用 Stream API ，就是太丑了。
        Map<Long, AuthMenuRespVO> treeNodeMap = new LinkedHashMap<>();
        menuList.forEach(menu -> treeNodeMap.put(menu.getId(), AuthConvert.INSTANCE.convertTreeNode(menu)));
        // 处理父子关系
        treeNodeMap.values().stream().filter(node -> !node.getParentId().equals(ID_ROOT)).forEach(childNode -> {
            // 获得父节点
            AuthMenuRespVO parentNode = treeNodeMap.get(childNode.getParentId());
            if (parentNode == null) {
                LoggerFactory.getLogger(getClass()).error("[buildRouterTree][resource({}) 找不到父资源({})]",
                    childNode.getId(), childNode.getParentId());
                return;
            }
            // 将自己添加到父节点中
            if (parentNode.getChildren() == null) {
                parentNode.setChildren(new ArrayList<>());
            }
            parentNode.getChildren().add(childNode);
        });
        // 获得到所有的根节点
        return CollUtil.filterList(treeNodeMap.values(), node -> ID_ROOT.equals(node.getParentId()));
    }

    SmsCodeSendReqDTO convert(AuthSmsSendReqVO reqVO);

    default SmsCodeUseReqDTO convert(AuthSmsLoginReqVO reqVO, SmsSceneEnum scene, String usedIp) {
        return SmsCodeUseReqDTO.builder()
            .mobile(reqVO.getMobile())
            .code(reqVO.getCode())
            .scene(scene)
            .usedIp(usedIp)
            .build();
    }

}
