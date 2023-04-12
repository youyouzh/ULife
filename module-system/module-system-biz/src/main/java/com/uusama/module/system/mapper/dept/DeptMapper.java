package com.uusama.module.system.mapper.dept;

import com.uusama.framework.mybatis.mapper.BaseMapperX;
import com.uusama.framework.mybatis.query.LambdaQueryWrapperX;
import com.uusama.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.uusama.module.system.entity.dept.DeptDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeptMapper extends BaseMapperX<DeptDO> {

    default List<DeptDO> selectList(DeptListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<DeptDO>()
                .likeIfPresent(DeptDO::getName, reqVO.getName())
                .eqIfPresent(DeptDO::getState, reqVO.getState()));
    }

    default DeptDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(DeptDO::getParentId, parentId, DeptDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(DeptDO::getParentId, parentId);
    }

}
