package com.uusama.module.system.convert.logger;

import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.module.system.controller.admin.logger.vo.loginlog.LoginLogExcelVO;
import com.uusama.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.uusama.module.system.entity.logger.LoginLogDO;
import com.uusama.module.system.logger.dto.LoginLogCreateReqDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LoginLogConvert {

    LoginLogConvert INSTANCE = Mappers.getMapper(LoginLogConvert.class);

    PageResult<LoginLogRespVO> convertPage(PageResult<LoginLogDO> page);

    List<LoginLogExcelVO> convertList(List<LoginLogDO> list);

    LoginLogDO convert(LoginLogCreateReqDTO bean);

}
