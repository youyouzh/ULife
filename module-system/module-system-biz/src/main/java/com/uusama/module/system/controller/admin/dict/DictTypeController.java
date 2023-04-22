package com.uusama.module.system.controller.admin.dict;

import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.framework.recorder.annotations.OperateLog;
import com.uusama.framework.recorder.enums.OperateTypeEnum;
import com.uusama.framework.tool.util.ExcelUtils;
import com.uusama.framework.web.pojo.CommonResult;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeExcelVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeExportReqVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeUpdateReqVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeCreateReqVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.uusama.module.system.convert.dict.DictTypeConvert;
import com.uusama.module.system.entity.dict.DictTypeDO;
import com.uusama.module.system.service.dict.DictTypeService;
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
import java.util.List;

@Tag(name = "管理后台 - 字典类型")
@RestController
@RequestMapping("/system/dict-type")
@Validated
public class DictTypeController {

    @Resource
    private DictTypeService dictTypeService;

    @PostMapping("/create")
    @Operation(summary = "创建字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:create')")
    public CommonResult<Long> createDictType(@Valid @RequestBody DictTypeCreateReqVO reqVO) {
        Long dictTypeId = dictTypeService.createDictType(reqVO);
        return CommonResult.success(dictTypeId);
    }

    @PutMapping("/update")
    @Operation(summary = "修改字典类型")
    @PreAuthorize("@ss.hasPermission('system:dict:update')")
    public CommonResult<Boolean> updateDictType(@Valid @RequestBody DictTypeUpdateReqVO reqVO) {
        dictTypeService.updateDictType(reqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除字典类型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:dict:delete')")
    public CommonResult<Boolean> deleteDictType(Long id) {
        dictTypeService.deleteDictType(id);
        return CommonResult.success(true);
    }

    @Operation(summary = "/获得字典类型的分页列表")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<PageResult<DictTypeRespVO>> pageDictTypes(@Valid DictTypePageReqVO reqVO) {
        return CommonResult.success(DictTypeConvert.INSTANCE.convertPage(dictTypeService.getDictTypePage(reqVO)));
    }

    @Operation(summary = "/查询字典类型详细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @GetMapping(value = "/get")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    public CommonResult<DictTypeRespVO> getDictType(@RequestParam("id") Long id) {
        return CommonResult.success(DictTypeConvert.INSTANCE.convert(dictTypeService.getDictType(id)));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获得全部字典类型列表", description = "包括开启 + 禁用的字典类型，主要用于前端的下拉选项")
    // 无需添加权限认证，因为前端全局都需要
    public CommonResult<List<DictTypeSimpleRespVO>> getSimpleDictTypeList() {
        List<DictTypeDO> list = dictTypeService.getDictTypeList();
        return CommonResult.success(DictTypeConvert.INSTANCE.convertList(list));
    }

    @Operation(summary = "导出数据类型")
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:dict:query')")
    @OperateLog(type = OperateTypeEnum.EXPORT)
    public void export(HttpServletResponse response, @Valid DictTypeExportReqVO reqVO) throws IOException {
        List<DictTypeDO> list = dictTypeService.getDictTypeList(reqVO);
        List<DictTypeExcelVO> data = DictTypeConvert.INSTANCE.convertList02(list);
        // 输出
        ExcelUtils.write(response, "字典类型.xls", "类型列表", DictTypeExcelVO.class, data);
    }

}
