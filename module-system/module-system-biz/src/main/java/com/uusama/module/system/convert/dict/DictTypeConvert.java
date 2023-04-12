package com.uusama.module.system.convert.dict;

import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeCreateReqVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeExcelVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeRespVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeSimpleRespVO;
import com.uusama.module.system.controller.admin.dict.vo.type.DictTypeUpdateReqVO;
import com.uusama.module.system.entity.dict.DictTypeDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DictTypeConvert {

    DictTypeConvert INSTANCE = Mappers.getMapper(DictTypeConvert.class);

    PageResult<DictTypeRespVO> convertPage(PageResult<DictTypeDO> bean);

    DictTypeRespVO convert(DictTypeDO bean);

    DictTypeDO convert(DictTypeCreateReqVO bean);

    DictTypeDO convert(DictTypeUpdateReqVO bean);

    List<DictTypeSimpleRespVO> convertList(List<DictTypeDO> list);

    List<DictTypeExcelVO> convertList02(List<DictTypeDO> list);

}
