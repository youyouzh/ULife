package com.uusama.module.system.redis;


import com.uusama.common.util.CollUtil;
import com.uusama.common.util.DateTimeUtil;
import com.uusama.framework.redis.core.RedisKeyDefine;
import com.uusama.framework.web.util.JsonUtils;
import com.uusama.module.system.entity.oauth2.OAuth2AccessTokenDO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.uusama.framework.redis.core.RedisKeyDefine.KeyTypeEnum.STRING;

/**
 * {@link OAuth2AccessTokenDO} 的 RedisDAO
 *
 * @author uusama
 */
@Repository
public class OAuth2AccessTokenRedisDAO {

    public static final RedisKeyDefine OAUTH2_ACCESS_TOKEN = new RedisKeyDefine("访问令牌的缓存",
                                                            "oauth2_access_token:%s", // 参数为访问令牌 token
                                                            STRING, OAuth2AccessTokenDO.class, RedisKeyDefine.TimeoutTypeEnum.DYNAMIC);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public OAuth2AccessTokenDO get(String accessToken) {
        String redisKey = formatKey(accessToken);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), OAuth2AccessTokenDO.class);
    }

    public void set(OAuth2AccessTokenDO accessTokenDO) {
        String redisKey = formatKey(accessTokenDO.getAccessToken());
        // 清理多余字段，避免缓存
        long time = DateTimeUtil.between(LocalDateTime.now(), accessTokenDO.getExpiresTime(), ChronoUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(redisKey, JsonUtils.toJsonString(accessTokenDO), time, TimeUnit.SECONDS);
    }

    public void delete(String accessToken) {
        String redisKey = formatKey(accessToken);
        stringRedisTemplate.delete(redisKey);
    }

    public void deleteList(Collection<String> accessTokens) {
        List<String> redisKeys = CollUtil.convertList(accessTokens, OAuth2AccessTokenRedisDAO::formatKey);
        stringRedisTemplate.delete(redisKeys);
    }

    private static String formatKey(String accessToken) {
        return String.format(OAUTH2_ACCESS_TOKEN.getKeyTemplate(), accessToken);
    }

}