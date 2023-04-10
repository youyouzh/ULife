package com.uusama.module.system.mapper.oauth2;

import com.uusama.framework.mybatis.mapper.BaseMapperX;
import com.uusama.module.system.entity.oauth2.OAuth2CodeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuth2CodeMapper extends BaseMapperX<OAuth2CodeDO> {

    default OAuth2CodeDO selectByCode(String code) {
        return selectOne(OAuth2CodeDO::getCode, code);
    }

}
