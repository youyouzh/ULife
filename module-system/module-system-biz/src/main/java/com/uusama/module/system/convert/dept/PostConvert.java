package com.uusama.module.system.convert.dept;

import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.module.system.controller.admin.dept.vo.post.PostCreateReqVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostExcelVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostRespVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostUpdateReqVO;
import com.uusama.module.system.entity.dept.PostDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PostConvert {

    PostConvert INSTANCE = Mappers.getMapper(PostConvert.class);

    List<PostSimpleRespVO> convertList02(List<PostDO> list);

    PageResult<PostRespVO> convertPage(PageResult<PostDO> page);

    PostRespVO convert(PostDO id);

    PostDO convert(PostCreateReqVO bean);

    PostDO convert(PostUpdateReqVO reqVO);

    List<PostExcelVO> convertList03(List<PostDO> list);

}
