package com.uusama.module.system.convert.logger;

import com.uusama.framework.api.constants.GlobalErrorCodeConstants;
import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.module.system.controller.admin.vo.operatelog.OperateLogExcelVO;
import com.uusama.module.system.controller.admin.vo.operatelog.OperateLogRespVO;
import com.uusama.module.system.entity.user.AdminUserDO;
import com.uusama.module.system.entity.logger.OperateLogDO;
import com.uusama.module.system.logger.dto.OperateLogCreateReqDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper
public interface OperateLogConvert {

    OperateLogConvert INSTANCE = Mappers.getMapper(OperateLogConvert.class);

    OperateLogDO convert(OperateLogCreateReqDTO bean);

    PageResult<OperateLogRespVO> convertPage(PageResult<OperateLogDO> page);

    OperateLogRespVO convert(OperateLogDO bean);

    default List<OperateLogExcelVO> convertList(List<OperateLogDO> list, Map<Long, AdminUserDO> userMap) {
        return list.stream().map(operateLog -> {
            OperateLogExcelVO excelVO = convert02(operateLog);
            userMap.entrySet().stream().filter(v -> v.getKey().equals(operateLog.getUserId()))
                    .findFirst().ifPresent(v -> excelVO.setUserNickname(v.getValue().getNickname());
            excelVO.setSuccessStr(GlobalErrorCodeConstants.SUCCESS.getCode().equals(operateLog.getResultCode()) ? "成功" : "失败");
            return excelVO;
        }).collect(Collectors.toList());
    }

    OperateLogExcelVO convert02(OperateLogDO bean);

}
