package com.uusama.module.system.convert.oauth2;

import com.uusama.module.system.controller.admin.oauth2.vo.user.OAuth2UserInfoRespVO;
import com.uusama.module.system.controller.admin.oauth2.vo.user.OAuth2UserUpdateReqVO;
import com.uusama.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.uusama.module.system.entity.dept.DeptDO;
import com.uusama.module.system.entity.dept.PostDO;
import com.uusama.module.system.entity.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OAuth2UserConvert {

    OAuth2UserConvert INSTANCE = Mappers.getMapper(OAuth2UserConvert.class);

    OAuth2UserInfoRespVO convert(AdminUserDO bean);

    OAuth2UserInfoRespVO.Dept convert(DeptDO dept);

    List<OAuth2UserInfoRespVO.Post> convertList(List<PostDO> list);

    UserProfileUpdateReqVO convert(OAuth2UserUpdateReqVO bean);

}
