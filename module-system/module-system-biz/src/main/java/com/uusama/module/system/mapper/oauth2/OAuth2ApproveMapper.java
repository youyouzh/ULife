package com.uusama.module.system.mapper.oauth2;

import com.uusama.framework.mybatis.mapper.BaseMapperX;
import com.uusama.framework.mybatis.query.LambdaQueryWrapperX;
import com.uusama.framework.web.enums.UserTypeEnum;
import com.uusama.module.system.entity.oauth2.OAuth2ApproveDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OAuth2ApproveMapper extends BaseMapperX<OAuth2ApproveDO> {

    default int update(OAuth2ApproveDO updateObj) {
        return update(updateObj, new LambdaQueryWrapperX<OAuth2ApproveDO>()
                .eq(OAuth2ApproveDO::getUserId, updateObj.getUserId())
                .eq(OAuth2ApproveDO::getUserType, updateObj.getUserType())
                .eq(OAuth2ApproveDO::getClientId, updateObj.getClientId())
                .eq(OAuth2ApproveDO::getScope, updateObj.getScope()));
    }

    default List<OAuth2ApproveDO> selectListByUserIdAndUserTypeAndClientId(Long userId, UserTypeEnum userType, String clientId) {
        return selectList(new LambdaQueryWrapperX<OAuth2ApproveDO>()
                .eq(OAuth2ApproveDO::getUserId, userId)
                .eq(OAuth2ApproveDO::getUserType, userType)
                .eq(OAuth2ApproveDO::getClientId, clientId));
    }

}
