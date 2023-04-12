package com.uusama.module.system.convert.user;

import com.uusama.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import com.uusama.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.uusama.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.uusama.module.system.controller.admin.user.vo.user.UserCreateReqVO;
import com.uusama.module.system.controller.admin.user.vo.user.UserExcelVO;
import com.uusama.module.system.controller.admin.user.vo.user.UserImportExcelVO;
import com.uusama.module.system.controller.admin.user.vo.user.UserPageItemRespVO;
import com.uusama.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import com.uusama.module.system.controller.admin.user.vo.user.UserUpdateReqVO;
import com.uusama.module.system.entity.dept.DeptDO;
import com.uusama.module.system.entity.dept.PostDO;
import com.uusama.module.system.entity.permission.RoleDO;
import com.uusama.module.system.entity.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    UserPageItemRespVO convert(AdminUserDO bean);

    UserPageItemRespVO.Dept convert(DeptDO bean);

    AdminUserDO convert(UserCreateReqVO bean);

    AdminUserDO convert(UserUpdateReqVO bean);

    UserExcelVO convert02(AdminUserDO bean);

    AdminUserDO convert(UserImportExcelVO bean);

    UserProfileRespVO convert03(AdminUserDO bean);

    List<UserProfileRespVO.Role> convertList(List<RoleDO> list);

    UserProfileRespVO.Dept convert02(DeptDO bean);

    AdminUserDO convert(UserProfileUpdateReqVO bean);

    AdminUserDO convert(UserProfileUpdatePasswordReqVO bean);

    List<UserProfileRespVO.Post> convertList02(List<PostDO> list);

    List<UserSimpleRespVO> convertList04(List<AdminUserDO> list);

}
