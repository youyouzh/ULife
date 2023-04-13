package com.uusama.module.system.controller.admin.logger;

import com.uusama.common.util.CollUtil;
import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.framework.recorder.annotations.OperateLog;
import com.uusama.framework.recorder.enums.OperateTypeEnum;
import com.uusama.framework.tool.util.ExcelUtils;
import com.uusama.framework.web.pojo.CommonResult;
import com.uusama.module.system.controller.admin.logger.vo.operatelog.OperateLogExcelVO;
import com.uusama.module.system.controller.admin.logger.vo.operatelog.OperateLogExportReqVO;
import com.uusama.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.uusama.module.system.controller.admin.logger.vo.operatelog.OperateLogRespVO;
import com.uusama.module.system.convert.logger.OperateLogConvert;
import com.uusama.module.system.entity.logger.OperateLogDO;
import com.uusama.module.system.entity.user.AdminUserDO;
import com.uusama.module.system.service.logger.OperateLogService;
import com.uusama.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Tag(name = "管理后台 - 操作日志")
@RestController
@RequestMapping("/system/operate-log")
@Validated
public class OperateLogController {

    @Resource
    private OperateLogService operateLogService;
    @Resource
    private AdminUserService userService;

    @GetMapping("/page")
    @Operation(summary = "查看操作日志分页列表")
    @PreAuthorize("@ss.hasPermission('system:operate-log:query')")
    public CommonResult<PageResult<OperateLogRespVO>> pageOperateLog(@Valid OperateLogPageReqVO reqVO) {
        PageResult<OperateLogDO> pageResult = operateLogService.getOperateLogPage(reqVO);

        // 获得拼接需要的数据
        Collection<Long> userIds = CollUtil.convertList(pageResult.getList(), OperateLogDO::getUserId);
        Map<Long, AdminUserDO> userMap = userService.getUserMap(userIds);
        // 拼接数据
        List<OperateLogRespVO> list = new ArrayList<>(pageResult.getList().size());
        pageResult.getList().forEach(operateLog -> {
            OperateLogRespVO respVO = OperateLogConvert.INSTANCE.convert(operateLog);
            list.add(respVO);
            // 拼接用户信息
            userMap.computeIfPresent(operateLog.getUserId(), (key, user) -> {
                respVO.setUserNickname(user.getNickname());
                return user;
            });
        });
        return CommonResult.success(new PageResult<>(list, pageResult.getTotal()));
    }

    @Operation(summary = "导出操作日志")
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:operate-log:export')")
    @OperateLog(type = OperateTypeEnum.EXPORT)
    public void exportOperateLog(HttpServletResponse response, @Valid OperateLogExportReqVO reqVO) throws IOException {
        List<OperateLogDO> list = operateLogService.getOperateLogList(reqVO);

        // 获得拼接需要的数据
        Collection<Long> userIds = CollUtil.convertList(list, OperateLogDO::getUserId);
        Map<Long, AdminUserDO> userMap = userService.getUserMap(userIds);
        // 拼接数据
        List<OperateLogExcelVO> excelDataList = OperateLogConvert.INSTANCE.convertList(list, userMap);
        // 输出
        ExcelUtils.write(response, "操作日志.xls", "数据列表", OperateLogExcelVO.class, excelDataList);
    }

}
