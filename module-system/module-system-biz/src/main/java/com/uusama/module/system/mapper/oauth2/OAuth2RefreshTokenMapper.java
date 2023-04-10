package com.uusama.module.system.mapper.oauth2;

import com.uusama.framework.mybatis.mapper.BaseMapperX;
import com.uusama.framework.mybatis.query.LambdaQueryWrapperX;
import com.uusama.module.system.entity.oauth2.OAuth2RefreshTokenDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2RefreshTokenMapper extends BaseMapperX<OAuth2RefreshTokenDO> {

    default int deleteByRefreshToken(String refreshToken) {
        return delete(new LambdaQueryWrapperX<OAuth2RefreshTokenDO>()
                .eq(OAuth2RefreshTokenDO::getRefreshToken, refreshToken));
    }

    default OAuth2RefreshTokenDO selectByRefreshToken(String refreshToken) {
        return selectOne(OAuth2RefreshTokenDO::getRefreshToken, refreshToken);
    }

}
