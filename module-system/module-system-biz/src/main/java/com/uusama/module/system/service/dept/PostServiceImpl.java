package com.uusama.module.system.service.dept;

import com.uusama.common.util.CollUtil;
import com.uusama.framework.mybatis.pojo.PageResult;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.framework.web.util.ParamUtils;
import com.uusama.module.system.controller.admin.dept.vo.post.PostCreateReqVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostExportReqVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.uusama.module.system.controller.admin.dept.vo.post.PostUpdateReqVO;
import com.uusama.module.system.convert.dept.PostConvert;
import com.uusama.module.system.entity.dept.PostDO;
import com.uusama.module.system.mapper.dept.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.uusama.module.system.constant.ErrorCodeConstants.POST_CODE_DUPLICATE;
import static com.uusama.module.system.constant.ErrorCodeConstants.POST_NAME_DUPLICATE;
import static com.uusama.module.system.constant.ErrorCodeConstants.POST_NOT_ENABLE;
import static com.uusama.module.system.constant.ErrorCodeConstants.POST_NOT_FOUND;

/**
 * 岗位 Service 实现类
 *
 * @author uusama
 */
@Service
@Validated
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostMapper postMapper;

    @Override
    public Long createPost(PostCreateReqVO reqVO) {
        // 校验正确性
        validatePostForCreateOrUpdate(null, reqVO.getName(), reqVO.getCode());

        // 插入岗位
        PostDO post = PostConvert.INSTANCE.convert(reqVO);
        postMapper.insert(post);
        return post.getId();
    }

    @Override
    public void updatePost(PostUpdateReqVO reqVO) {
        // 校验正确性
        validatePostForCreateOrUpdate(reqVO.getId(), reqVO.getName(), reqVO.getCode());

        // 更新岗位
        PostDO updateObj = PostConvert.INSTANCE.convert(reqVO);
        postMapper.updateById(updateObj);
    }

    @Override
    public void deletePost(Long id) {
        // 校验是否存在
        validatePostExists(id);
        // 删除部门
        postMapper.deleteById(id);
    }

    private void validatePostForCreateOrUpdate(Long id, String name, String code) {
        // 校验自己存在
        validatePostExists(id);
        // 校验岗位名的唯一性
        validatePostNameUnique(id, name);
        // 校验岗位编码的唯一性
        validatePostCodeUnique(id, code);
    }

    private void validatePostNameUnique(Long id, String name) {
        PostDO post = postMapper.selectByName(name);
        if (post == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        ParamUtils.checkNotNull(id, POST_NAME_DUPLICATE);
        ParamUtils.checkMatch(id.equals(post.getId()), POST_NAME_DUPLICATE);
    }

    private void validatePostCodeUnique(Long id, String code) {
        PostDO post = postMapper.selectByCode(code);
        if (post == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的岗位
        ParamUtils.checkNotNull(id, POST_CODE_DUPLICATE);
        ParamUtils.checkMatch(id.equals(post.getId()), POST_CODE_DUPLICATE);
    }

    private void validatePostExists(Long id) {
        if (id == null) {
            return;
        }
        ParamUtils.checkNotNull(postMapper.selectById(id), POST_NOT_FOUND);
    }

    @Override
    public List<PostDO> getPostList(Collection<Long> ids, Collection<CommonState> states) {
        return postMapper.selectList(ids, states);
    }

    @Override
    public PageResult<PostDO> getPostPage(PostPageReqVO reqVO) {
        return postMapper.selectPage(reqVO);
    }

    @Override
    public List<PostDO> getPostList(PostExportReqVO reqVO) {
        return postMapper.selectList(reqVO);
    }

    @Override
    public PostDO getPost(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public void validatePostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得岗位信息
        List<PostDO> posts = postMapper.selectBatchIds(ids);
        Map<Long, PostDO> postMap = CollUtil.convertMap(posts, PostDO::getId);
        // 校验
        ids.forEach(id -> {
            PostDO post = postMap.get(id);
            ParamUtils.checkNotNull(post, POST_NOT_FOUND);
            ParamUtils.checkEnable(post.getState(), POST_NOT_ENABLE);
        });
    }
}
