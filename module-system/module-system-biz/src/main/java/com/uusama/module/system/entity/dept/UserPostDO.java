package com.uusama.module.system.entity.dept;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.framework.mybatis.entity.BaseDO;
import com.uusama.module.system.entity.user.AdminUserDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 用户和岗位关联
 *
 * @author uusama
 */
@TableName("system_user_post")
@KeySequence("system_user_post_seq")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserPostDO extends BaseDO {
    /**
     * 用户 ID
     * 关联 {@link AdminUserDO#getId()}
     */
    private Long userId;
    /**
     * 角色 ID
     * 关联 {@link PostDO#getId()}
     */
    private Long postId;

}
