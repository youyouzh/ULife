package com.uusama.module.system.service.dept;

import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.module.system.controller.admin.dept.vo.post.PostCreateReqVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostExportReqVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostUpdateReqVO;
import com.uusama.module.system.entity.dept.PostDO;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 岗位 Service 接口
 *
 * @author uusama
 */
public interface PostService {

    /**
     * 创建岗位
     *
     * @param reqVO 岗位信息
     * @return 岗位编号
     */
    Long createPost(PostCreateReqVO reqVO);

    /**
     * 更新岗位
     *
     * @param reqVO 岗位信息
     */
    void updatePost(PostUpdateReqVO reqVO);

    /**
     * 删除岗位信息
     *
     * @param id 岗位编号
     */
    void deletePost(Long id);

    /**
     * 获得岗位列表
     *
     * @param ids 岗位编号数组。如果为空，不进行筛选
     * @return 部门列表
     */
    default List<PostDO> getPostList(@Nullable Collection<Long> ids) {
        return getPostList(ids, Arrays.asList(CommonState.ENABLE, CommonState.DISABLE));
    }

    /**
     * 获得符合条件的岗位列表
     *
     * @param ids 岗位编号数组。如果为空，不进行筛选
     * @param states 状态数组。如果为空，不进行筛选
     * @return 部门列表
     */
    List<PostDO> getPostList(@Nullable Collection<Long> ids, @Nullable Collection<CommonState> states);

    /**
     * 获得岗位分页列表
     *
     * @param reqVO 分页条件
     * @return 部门分页列表
     */
    PageResult<PostDO> getPostPage(PostPageReqVO reqVO);

    /**
     * 获得岗位列表
     *
     * @param reqVO 查询条件
     * @return 部门列表
     */
    List<PostDO> getPostList(PostExportReqVO reqVO);

    /**
     * 获得岗位信息
     *
     * @param id 岗位编号
     * @return 岗位信息
     */
    PostDO getPost(Long id);

    /**
     * 校验岗位们是否有效。如下情况，视为无效：
     * 1. 岗位编号不存在
     * 2. 岗位被禁用
     *
     * @param ids 岗位编号数组
     */
    void validatePostList(Collection<Long> ids);

}
