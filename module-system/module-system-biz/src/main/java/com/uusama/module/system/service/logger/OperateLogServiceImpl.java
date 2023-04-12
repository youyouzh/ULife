package com.uusama.module.system.service.logger;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.string.StrUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.uusama.common.util.CollUtil;
import com.uusama.common.util.StrUtil;
import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.module.system.controller.admin.vo.operatelog.OperateLogExportReqVO;
import com.uusama.module.system.controller.admin.vo.operatelog.OperateLogPageReqVO;
import com.uusama.module.system.convert.logger.OperateLogConvert;
import com.uusama.module.system.entity.logger.OperateLogDO;
import com.uusama.module.system.logger.dto.OperateLogCreateReqDTO;
import com.uusama.module.system.mapper.auth.AdminUserMapper;
import com.uusama.module.system.mapper.logger.OperateLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.system.dal.dataobject.logger.OperateLogDO.JAVA_METHOD_ARGS_MAX_LENGTH;
import static cn.iocoder.yudao.module.system.dal.dataobject.logger.OperateLogDO.RESULT_MAX_LENGTH;
import static com.uusama.module.system.entity.logger.OperateLogDO.JAVA_METHOD_ARGS_MAX_LENGTH;
import static com.uusama.module.system.entity.logger.OperateLogDO.RESULT_MAX_LENGTH;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class OperateLogServiceImpl implements OperateLogService {
    private final OperateLogMapper operateLogMapper;
    private final AdminUserMapper adminUserMapper;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO logDO = OperateLogConvert.INSTANCE.convert(createReqDTO);
        logDO.setJavaMethodArgs(StrUtil.maxLength(logDO.getJavaMethodArgs(), JAVA_METHOD_ARGS_MAX_LENGTH));
        logDO.setResultData(StrUtil.maxLength(logDO.getResultData(), RESULT_MAX_LENGTH));
        operateLogMapper.insert(logDO);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO reqVO) {
        // 处理基于用户昵称的查询
        Collection<Long> userIds = null;
        if (StrUtil.isNotEmpty(reqVO.getUserNickname())) {
            userIds = CollUtil.convertSet(adminUserMapper.selectListByNickname(reqVO.getUserNickname()), AdminUserDO::getId);
            if (CollUtil.isEmpty(userIds)) {
                return PageResult.empty();
            }
        }
        // 查询分页
        return operateLogMapper.selectPage(reqVO, userIds);
    }

    @Override
    public List<OperateLogDO> getOperateLogList(OperateLogExportReqVO reqVO) {
        // 处理基于用户昵称的查询
        Collection<Long> userIds = null;
        if (StrUtil.isNotEmpty(reqVO.getUserNickname())) {
            userIds = CollUtil.convertSet(adminUserMapper.selectListByNickname(reqVO.getUserNickname()), AdminUserDO::getId);
            if (CollUtil.isEmpty(userIds)) {
                return Collections.emptyList();
            }
        }
        // 查询列表
        return operateLogMapper.selectList(reqVO, userIds);
    }

}
