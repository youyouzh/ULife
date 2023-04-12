package com.uusama.module.system.service.logger;

import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.module.system.controller.admin.vo.operatelog.OperateLogExportReqVO;
import com.uusama.module.system.controller.admin.vo.operatelog.OperateLogPageReqVO;
import com.uusama.module.system.entity.logger.OperateLogDO;
import com.uusama.module.system.logger.dto.OperateLogCreateReqDTO;

import java.util.List;

/**
 * 操作日志 Service 接口
 *
 * @author 芋道源码
 */
public interface OperateLogService {

    /**
     * 记录操作日志
     *
     * @param createReqDTO 操作日志请求
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 获得操作日志分页列表
     *
     * @param reqVO 分页条件
     * @return 操作日志分页列表
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO reqVO);

    /**
     * 获得操作日志列表
     *
     * @param reqVO 列表条件
     * @return 日志列表
     */
    List<OperateLogDO> getOperateLogList(OperateLogExportReqVO reqVO);

}
