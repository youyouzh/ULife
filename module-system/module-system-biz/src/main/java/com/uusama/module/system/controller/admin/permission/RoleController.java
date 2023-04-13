package com.uusama.module.system.controller.admin.permission;

import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.framework.recorder.annotations.OperateLog;
import com.uusama.framework.recorder.enums.OperateTypeEnum;
import com.uusama.framework.tool.util.ExcelUtils;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.framework.web.pojo.CommonResult;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleCreateReqVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleExcelVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleExportReqVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleRespVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleUpdateReqVO;
import com.uusama.module.system.controller.admin.permission.vo.role.RoleUpdateStatusReqVO;
import com.uusama.module.system.convert.permission.RoleConvert;
import com.uusama.module.system.entity.permission.RoleDO;
import com.uusama.module.system.service.permission.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.singleton;

@Tag(name = "管理后台 - 角色")
@RestController
@RequestMapping("/system/role")
@Validated
public class RoleController {

    @Resource
    private RoleService roleService;

    @PostMapping("/create")
    @Operation(summary = "创建角色")
    @PreAuthorize("@ss.hasPermission('system:role:create')")
    public CommonResult<Long> createRole(@Valid @RequestBody RoleCreateReqVO reqVO) {
        return CommonResult.success(roleService.createRole(reqVO, null));
    }

    @PutMapping("/update")
    @Operation(summary = "修改角色")
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    public CommonResult<Boolean> updateRole(@Valid @RequestBody RoleUpdateReqVO reqVO) {
        roleService.updateRole(reqVO);
        return CommonResult.success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "修改角色状态")
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    public CommonResult<Boolean> updateRoleStatus(@Valid @RequestBody RoleUpdateStatusReqVO reqVO) {
        roleService.updateRoleStatus(reqVO.getId(), reqVO.getState());
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除角色")
    @Parameter(name = "id", description = "角色编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    public CommonResult<Boolean> deleteRole(@RequestParam("id") Long id) {
        roleService.deleteRole(id);
        return CommonResult.success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<RoleRespVO> getRole(@RequestParam("id") Long id) {
        RoleDO role = roleService.getRole(id);
        return CommonResult.success(RoleConvert.INSTANCE.convert(role));
    }

    @GetMapping("/page")
    @Operation(summary = "获得角色分页")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<PageResult<RoleDO>> getRolePage(RolePageReqVO reqVO) {
        return CommonResult.success(roleService.getRolePage(reqVO));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获取角色精简信息列表", description = "只包含被开启的角色，主要用于前端的下拉选项")
    public CommonResult<List<RoleSimpleRespVO>> getSimpleRoleList() {
        // 获得角色列表，只要开启状态的
        List<RoleDO> list = roleService.getRoleListByStatus(singleton(CommonState.ENABLE));
        // 排序后，返回给前端
        list.sort(Comparator.comparing(RoleDO::getSort));
        return CommonResult.success(RoleConvert.INSTANCE.convertList02(list));
    }

    @GetMapping("/export")
    @OperateLog(type = OperateTypeEnum.EXPORT)
    @PreAuthorize("@ss.hasPermission('system:role:export')")
    public void export(HttpServletResponse response, @Validated RoleExportReqVO reqVO) throws IOException {
        List<RoleDO> list = roleService.getRoleList(reqVO);
        List<RoleExcelVO> data = RoleConvert.INSTANCE.convertList03(list);
        // 输出
        ExcelUtils.write(response, "角色数据.xls", "角色列表", RoleExcelVO.class, data);
    }

}
