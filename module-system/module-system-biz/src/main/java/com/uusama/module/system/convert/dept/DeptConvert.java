package com.uusama.module.system.convert.dept;

import com.uusama.module.system.controller.admin.dept.vo.dept.DeptCreateReqVO;
import com.uusama.module.system.controller.admin.dept.vo.dept.DeptRespVO;
import com.uusama.module.system.controller.admin.dept.vo.dept.DeptSimpleRespVO;
import com.uusama.module.system.controller.admin.dept.vo.dept.DeptUpdateReqVO;
import com.uusama.module.system.entity.dept.DeptDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DeptConvert {

    DeptConvert INSTANCE = Mappers.getMapper(DeptConvert.class);

    List<DeptRespVO> convertList(List<DeptDO> list);

    List<DeptSimpleRespVO> convertList02(List<DeptDO> list);

    DeptRespVO convert(DeptDO bean);

    DeptDO convert(DeptCreateReqVO bean);

    DeptDO convert(DeptUpdateReqVO bean);

}
