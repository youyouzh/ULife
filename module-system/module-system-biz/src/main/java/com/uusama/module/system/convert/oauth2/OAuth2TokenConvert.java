package com.uusama.module.system.convert.oauth2;

import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.framework.security.api.dto.OAuth2AccessTokenCheckRespDTO;
import com.uusama.framework.security.api.dto.OAuth2AccessTokenRespDTO;
import com.uusama.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenRespVO;
import com.uusama.module.system.entity.oauth2.OAuth2AccessTokenDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OAuth2TokenConvert {

    OAuth2TokenConvert INSTANCE = Mappers.getMapper(OAuth2TokenConvert.class);

    OAuth2AccessTokenCheckRespDTO convert(OAuth2AccessTokenDO bean);

    PageResult<OAuth2AccessTokenRespVO> convert(PageResult<OAuth2AccessTokenDO> page);

    OAuth2AccessTokenRespDTO convert2(OAuth2AccessTokenDO bean);

}
