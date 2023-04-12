package com.uusama.module.system.mapper.permission;

import com.uusama.framework.mybatis.mapper.BaseMapperX;
import com.uusama.framework.mybatis.query.LambdaQueryWrapperX;
import com.uusama.module.system.controller.admin.vo.menu.MenuListReqVO;
import com.uusama.module.system.entity.permission.MenuDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapperX<MenuDO> {

    default MenuDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(MenuDO::getParentId, parentId, MenuDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(MenuDO::getParentId, parentId);
    }

    default List<MenuDO> selectList(MenuListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<MenuDO>()
                .likeIfPresent(MenuDO::getName, reqVO.getName())
                .eqIfPresent(MenuDO::getState, reqVO.getState()));
    }

}
