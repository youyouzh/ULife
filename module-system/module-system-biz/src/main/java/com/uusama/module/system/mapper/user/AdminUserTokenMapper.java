package com.uusama.module.system.mapper.user;

import com.uusama.framework.mybatis.mapper.BaseMapperX;
import com.uusama.module.system.entity.user.AdminUserTokenDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminUserTokenMapper extends BaseMapperX<AdminUserTokenDO> {

    default AdminUserTokenDO selectByRefreshTokenAndClientId(String refreshToken, String clientId) {
        return selectOne(AdminUserTokenDO::getRefreshToken, refreshToken, AdminUserTokenDO::getClientId, clientId);
    }

}
