package com.uusama.module.system.service.oauth2;

import com.uusama.common.util.CollUtil;
import com.uusama.framework.web.enums.UserTypeEnum;
import com.uusama.module.system.entity.oauth2.OAuth2ApproveDO;
import com.uusama.module.system.entity.oauth2.OAuth2ClientDO;
import com.uusama.module.system.mapper.oauth2.OAuth2ApproveMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * OAuth2 批准 Service 实现类
 *
 * @author uusama
 */
@Service
@Validated
public class OAuth2ApproveServiceImpl implements OAuth2ApproveService {

    /**
     * 批准的过期时间，默认 30 天
     */
    private static final Integer TIMEOUT = 30 * 24 * 60 * 60; // 单位：秒

    @Resource
    private OAuth2ClientService oauth2ClientService;

    @Resource
    private OAuth2ApproveMapper oauth2ApproveMapper;

    @Override
    @Transactional
    public boolean checkForPreApproval(Long userId, UserTypeEnum userType, String clientId, Collection<String> requestedScopes) {
        // 第一步，基于 Client 的自动授权计算，如果 scopes 都在自动授权中，则返回 true 通过
        OAuth2ClientDO clientDO = oauth2ClientService.validOAuthClientFromCache(clientId);
        Assert.notNull(clientDO, "客户端不能为空"); // 防御性编程
        if (CollectionUtils.containsAll(clientDO.getAutoApproveScopes(), requestedScopes)) {
            // gh-877 - if all scopes are auto approved, approvals still need to be added to the approval store.
            LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT);
            for (String scope : requestedScopes) {
                saveApprove(userId, userType, clientId, scope, true, expireTime);
            }
            return true;
        }

        // 第二步，算上用户已经批准的授权。如果 scopes 都包含，则返回 true
        List<OAuth2ApproveDO> approveDOs = getApproveList(userId, userType, clientId);
        Set<String> scopes = CollUtil.convertSet(approveDOs, OAuth2ApproveDO::getScope,
                                                 OAuth2ApproveDO::getApproved); // 只保留未过期的 + 同意的
        return CollectionUtils.containsAll(scopes, requestedScopes);
    }

    @Override
    @Transactional
    public boolean updateAfterApproval(Long userId, UserTypeEnum userType, String clientId, Map<String, Boolean> requestedScopes) {
        // 如果 requestedScopes 为空，说明没有要求，则返回 true 通过
        if (MapUtils.isEmpty(requestedScopes)) {
            return true;
        }

        // 更新批准的信息
        boolean success = false; // 需要至少有一个同意
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT);
        for (Map.Entry<String, Boolean> entry : requestedScopes.entrySet()) {
            if (entry.getValue()) {
                success = true;
            }
            saveApprove(userId, userType, clientId, entry.getKey(), entry.getValue(), expireTime);
        }
        return success;
    }

    @Override
    public List<OAuth2ApproveDO> getApproveList(Long userId, UserTypeEnum userType, String clientId) {
        List<OAuth2ApproveDO> approveDOs = oauth2ApproveMapper.selectListByUserIdAndUserTypeAndClientId(
                userId, userType, clientId);
        approveDOs.removeIf(o -> LocalDateTime.now().isAfter(o.getExpiresTime()));
        return approveDOs;
    }

    void saveApprove(Long userId, UserTypeEnum userType, String clientId,
                     String scope, Boolean approved, LocalDateTime expireTime) {
        // 先更新
        OAuth2ApproveDO approveDO = OAuth2ApproveDO.builder().userId(userId).userType(userType)
                .clientId(clientId).scope(scope).approved(approved).expiresTime(expireTime).build();
        if (oauth2ApproveMapper.update(approveDO) == 1) {
            return;
        }
        // 失败，则说明不存在，进行更新
        oauth2ApproveMapper.insert(approveDO);
    }

}
