package com.uusama.framework.mybatis.handler;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.uusama.framework.mybatis.entity.BaseDO;
import com.uusama.framework.mybatis.entity.BaseConfigDO;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 通用参数填充实现类
 *
 * 如果没有显式的对通用参数进行赋值，这里会对通用参数进行填充、赋值
 *
 * @author uusama
 */
public class DefaultDBFieldHandler implements MetaObjectHandler {
    private static CurrentLoginUserGetter currentLoginUserGetter;

    public static void registerLoginUserGetter(CurrentLoginUserGetter currentLoginUserGetter) {
        DefaultDBFieldHandler.currentLoginUserGetter = currentLoginUserGetter;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseDO) {
            BaseDO baseDO = (BaseDO) metaObject.getOriginalObject();

            LocalDateTime current = LocalDateTime.now();
            // 创建时间为空，则以当前时间为插入时间
            if (Objects.isNull(baseDO.getCreateTime())) {
                baseDO.setCreateTime(current);
            }
            // 更新时间为空，则以当前时间为更新时间
            if (Objects.isNull(baseDO.getUpdateTime())) {
                baseDO.setUpdateTime(current);
            }

            fillLoginUser(metaObject);
        }
    }

    private void fillLoginUser(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseConfigDO) {
            BaseConfigDO baseConfigDO = (BaseConfigDO) metaObject.getOriginalObject();
            Long userId = getLoginUserId();
            // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
            if (Objects.nonNull(userId) && Objects.isNull(baseConfigDO.getCreator())) {
                baseConfigDO.setCreator(userId.toString());
            }
            // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
            if (Objects.nonNull(userId) && Objects.isNull(baseConfigDO.getUpdater())) {
                baseConfigDO.setUpdater(userId.toString());
            }
        }
    }

    private Long getLoginUserId() {
        return null == currentLoginUserGetter ? null : currentLoginUserGetter.getLoginUserId();
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间为空，则以当前时间为更新时间
        Object modifyTime = getFieldValByName("updateTime", metaObject);
        if (Objects.isNull(modifyTime)) {
            setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }

        // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
        Object modifier = getFieldValByName("updater", metaObject);
        Long userId = getLoginUserId();
        if (Objects.nonNull(userId) && Objects.isNull(modifier)) {
            setFieldValByName("updater", userId.toString(), metaObject);
        }
    }
}