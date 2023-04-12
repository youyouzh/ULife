package com.uusama.module.system.convert.dict;

import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.module.system.controller.admin.dict.vo.data.DictDataCreateReqVO;
import com.uusama.module.system.controller.admin.dict.vo.data.DictDataExcelVO;
import com.uusama.module.system.controller.admin.dict.vo.data.DictDataRespVO;
import com.uusama.module.system.controller.admin.dict.vo.data.DictDataSimpleRespVO;
import com.uusama.module.system.controller.admin.dict.vo.data.DictDataUpdateReqVO;
import com.uusama.module.system.entity.dict.DictDataDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DictDataConvert {

    DictDataConvert INSTANCE = Mappers.getMapper(DictDataConvert.class);

    List<DictDataSimpleRespVO> convertList(List<DictDataDO> list);

    DictDataRespVO convert(DictDataDO bean);

    PageResult<DictDataRespVO> convertPage(PageResult<DictDataDO> page);

    DictDataDO convert(DictDataUpdateReqVO bean);

    DictDataDO convert(DictDataCreateReqVO bean);

    List<DictDataExcelVO> convertList02(List<DictDataDO> bean);

}
