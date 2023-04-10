package com.uusama.module.system.mapper.oauth2;

import com.uusama.framework.mybatis.mapper.BaseMapperX;
import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.framework.mybatis.query.LambdaQueryWrapperX;
import com.uusama.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import com.uusama.module.system.entity.oauth2.OAuth2AccessTokenDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OAuth2AccessTokenMapper extends BaseMapperX<OAuth2AccessTokenDO> {

    default OAuth2AccessTokenDO selectByAccessToken(String accessToken) {
        return selectOne(OAuth2AccessTokenDO::getAccessToken, accessToken);
    }

    default List<OAuth2AccessTokenDO> selectListByRefreshToken(String refreshToken) {
        return selectList(OAuth2AccessTokenDO::getRefreshToken, refreshToken);
    }

    default PageResult<OAuth2AccessTokenDO> selectPage(OAuth2AccessTokenPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OAuth2AccessTokenDO>()
                .eqIfPresent(OAuth2AccessTokenDO::getUserId, reqVO.getUserId())
                .eqIfPresent(OAuth2AccessTokenDO::getUserType, reqVO.getUserType())
                .likeIfPresent(OAuth2AccessTokenDO::getClientId, reqVO.getClientId())
                .gt(OAuth2AccessTokenDO::getExpiresTime, LocalDateTime.now())
                .orderByDesc(OAuth2AccessTokenDO::getId));
    }

}
