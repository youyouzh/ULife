package com.uusama.module.system.entity.user;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.uusama.framework.mybatis.entity.BaseConfigDO;
import com.uusama.framework.mybatis.type.JsonLongSetTypeHandler;
import com.uusama.framework.web.enums.CommonState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 管理后台的用户 DO
 * 由于 SQL Server 的 system_user 是关键字，所以使用 system_users
 *
 * @author uusama
 */
@TableName(value = "system_users", autoResultMap = true)
@KeySequence("system_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AdminUserDO extends BaseConfigDO {

    /**
     * 用户账号
     */
    private String username;
    /**
     * 加密后的密码
     *
     * 因为目前使用 {@link BCryptPasswordEncoder} 加密器，所以无需自己处理 salt 盐
     */
    private String password;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 备注
     */
    private String remark;
    /**
     * 部门 ID
     */
    private Long deptId;
    /**
     * 岗位编号数组
     */
    @TableField(typeHandler = JsonLongSetTypeHandler.class)
    private Set<Long> postIds;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 用户性别
     */
    @EnumValue
    private SexEnum sex;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 帐号状态
     */
    @EnumValue
    private CommonState state;
    /**
     * 最后登录IP
     */
    private String loginIp;
    /**
     * 最后登录时间
     */
    private LocalDateTime loginDate;

    /**
     * 性别的枚举值
     *
     * @author uusama
     */
    @Getter
    @AllArgsConstructor
    public enum SexEnum {

        /** 男 */
        MALE(1),
        /** 女 */
        FEMALE(2),
        /* 未知 */
        UNKNOWN(3);

        /**
         * 性别
         */
        private final Integer sex;
//        private final String label;
    }
}
