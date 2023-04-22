-- create database ulife;
use ulife;

-- dict --------------------
DROP TABLE IF EXISTS `system_dict_data`;
CREATE TABLE `system_dict_data`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    `sort`        int          NOT NULL DEFAULT 0 COMMENT '字典排序',
    `label`       varchar(100) NOT NULL DEFAULT '' COMMENT '字典标签',
    `value`       varchar(100) NOT NULL DEFAULT '' COMMENT '字典键值',
    `dict_type`   varchar(100) NOT NULL DEFAULT '' COMMENT '字典类型',
    `state`       varchar(16)  NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
    `color_type`  varchar(100) NULL     DEFAULT '' COMMENT '颜色类型',
    `css_class`   varchar(100) NULL     DEFAULT '' COMMENT 'css 样式',
    `remark`      varchar(255) NULL     DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1229
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '字典数据表';



DROP TABLE IF EXISTS `system_dict_type`;
CREATE TABLE `system_dict_type`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `name`         varchar(100) NOT NULL DEFAULT '' COMMENT '字典名称',
    `type`         varchar(100) NOT NULL DEFAULT '' COMMENT '字典类型',
    `state`        varchar(16)  NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
    `remark`       varchar(255) NULL     DEFAULT NULL COMMENT '备注',
    `creator`      varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `deleted_time` datetime     NULL     DEFAULT NULL COMMENT '删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `dict_type` (`type` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 168
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型表';


-- 用户信息表 ------
DROP TABLE IF EXISTS `system_users`;
CREATE TABLE `system_users`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    varchar(30)  NOT NULL COMMENT '用户账号',
    `password`    varchar(100) NOT NULL DEFAULT '' COMMENT '密码',
    `nickname`    varchar(30)  NOT NULL COMMENT '用户昵称',
    `remark`      varchar(255) NULL     DEFAULT NULL COMMENT '备注',
    `dept_id`     bigint       NULL     DEFAULT NULL COMMENT '部门ID',
    `post_ids`    varchar(255) NULL     DEFAULT NULL COMMENT '岗位编号数组',
    `email`       varchar(50)  NULL     DEFAULT '' COMMENT '用户邮箱',
    `mobile`      varchar(11)  NULL     DEFAULT '' COMMENT '手机号码',
    `sex`         varchar(16)  NOT NULL DEFAULT 'UNKNOWN' COMMENT '用户性别',
    `avatar`      varchar(100) NULL     DEFAULT '' COMMENT '头像地址',
    `state`       varchar(16)  NOT NULL DEFAULT 'ENABLE' COMMENT '帐号状态',
    `login_ip`    varchar(50)  NULL     DEFAULT '' COMMENT '最后登录IP',
    `login_date`  datetime     NULL     DEFAULT NULL COMMENT '最后登录时间',
    `creator`     varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX (`username` ASC, `update_time` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 123
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表';

INSERT INTO `system_users` (`id`, `username`, `password`, `nickname`, `remark`, `dept_id`, `post_ids`, `email`,
                            `mobile`, `sex`, `avatar`, `state`, `login_ip`, `login_date`, `creator`, `create_time`,
                            `updater`, `update_time`, `deleted`)
VALUES (1, 'admin', '$2a$10$mRMIYLDtRHlf6.9ipiqH1.Z.bh/R9dO9d5iHiGYPigi6r5KOoR2Wm', 'uusama', '管理员', 103, '[1]',
        'aoteman@126.com', '15612345678', 'MALE',
        'http://test.yudao.iocoder.cn/e1fdd7271685ec143a0900681606406621717a666ad0b2798b096df41422b32f.png', 'ENABLE',
        '0:0:0:0:0:0:0:1', '2023-02-10 13:49:49', 'admin', '2021-01-05 17:03:47', NULL, '2023-02-10 13:49:49', b'0');


-- dept post -----------------
DROP TABLE IF EXISTS `system_dept`;
CREATE TABLE `system_dept`
(
    `id`             bigint      NOT NULL AUTO_INCREMENT COMMENT '部门id',
    `name`           varchar(30) NOT NULL DEFAULT '' COMMENT '部门名称',
    `parent_id`      bigint      NOT NULL DEFAULT 0 COMMENT '父部门id',
    `sort`           int         NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `leader_user_id` bigint      NULL     DEFAULT NULL COMMENT '负责人',
    `phone`          varchar(11) NULL     DEFAULT NULL COMMENT '联系电话',
    `email`          varchar(50) NULL     DEFAULT NULL COMMENT '邮箱',
    `state`          varchar(16) NOT NULL DEFAULT 'ENABLE' COMMENT '部门状态',
    `creator`        varchar(64) NULL     DEFAULT '' COMMENT '创建者',
    `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        varchar(64) NULL     DEFAULT '' COMMENT '更新者',
    `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        bit(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 112
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '部门表';

DROP TABLE IF EXISTS `system_post`;
CREATE TABLE `system_post`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `code`        varchar(64)  NOT NULL COMMENT '岗位编码',
    `name`        varchar(50)  NOT NULL COMMENT '岗位名称',
    `sort`        int          NOT NULL COMMENT '显示顺序',
    `state`       varchar(16)  NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
    `remark`      varchar(255) NULL     DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '岗位信息表';

DROP TABLE IF EXISTS `system_user_post`;
CREATE TABLE `system_user_post`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`     bigint      NOT NULL DEFAULT 0 COMMENT '用户ID',
    `post_id`     bigint      NOT NULL DEFAULT 0 COMMENT '岗位ID',
    `creator`     varchar(64) NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64) NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 118
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户岗位表';



DROP TABLE IF EXISTS `system_user_role`;
CREATE TABLE `system_user_role`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '自增编号',
    `user_id`     bigint      NOT NULL COMMENT '用户ID',
    `role_id`     bigint      NOT NULL COMMENT '角色ID',
    `creator`     varchar(64) NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64) NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)      NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 28
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户和角色关联表';


DROP TABLE IF EXISTS `system_menu`;
CREATE TABLE `system_menu`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `name`           varchar(50)  NOT NULL COMMENT '菜单名称',
    `permission`     varchar(100) NOT NULL DEFAULT '' COMMENT '权限标识',
    `type`           varchar(32)  NOT NULL COMMENT '菜单类型',
    `sort`           int          NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `parent_id`      bigint       NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    `path`           varchar(200) NULL     DEFAULT '' COMMENT '路由地址',
    `icon`           varchar(100) NULL     DEFAULT '#' COMMENT '菜单图标',
    `component`      varchar(255) NULL     DEFAULT NULL COMMENT '组件路径',
    `component_name` varchar(255) NULL     DEFAULT NULL COMMENT '组件名',
    `state`          varchar(16)  NOT NULL DEFAULT 'ENABLE' COMMENT '菜单状态',
    `visible`        bit(1)       NOT NULL DEFAULT b'1' COMMENT '是否可见',
    `keep_alive`     bit(1)       NOT NULL DEFAULT b'1' COMMENT '是否缓存',
    `always_show`    bit(1)       NOT NULL DEFAULT b'1' COMMENT '是否总是显示',
    `creator`        varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2159
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表';


DROP TABLE IF EXISTS `system_role`;
CREATE TABLE `system_role`
(
    `id`                  bigint       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name`                varchar(30)  NOT NULL COMMENT '角色名称',
    `code`                varchar(100) NOT NULL COMMENT '角色权限字符串',
    `sort`                int          NOT NULL COMMENT '显示顺序',
    `data_scope`          varchar(32)  NOT NULL COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    `data_scope_dept_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '数据范围(指定部门数组)',
    `state`               varchar(16)  NOT NULL DEFAULT 'ENABLE' COMMENT '角色状态',
    `type`                varchar(32)  NOT NULL COMMENT '角色类型',
    `remark`              varchar(255) NULL     DEFAULT NULL COMMENT '备注',
    `creator`             varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`             varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 119
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表';


DROP TABLE IF EXISTS `system_role_menu`;
CREATE TABLE `system_role_menu`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '自增编号',
    `role_id`     bigint      NOT NULL COMMENT '角色ID',
    `menu_id`     bigint      NOT NULL COMMENT '菜单ID',
    `creator`     varchar(64) NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64) NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2313
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '角色和菜单关联表';



DROP TABLE IF EXISTS `system_user_session`;
CREATE TABLE `system_user_session`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `token`           varchar(32)  NOT NULL COMMENT '会话编号',
    `user_id`         bigint       NOT NULL COMMENT '用户编号',
    `user_type`       varchar(16)  NOT NULL DEFAULT 0 COMMENT '用户类型',
    `session_timeout` datetime     NOT NULL COMMENT '会话超时时间',
    `username`        varchar(30)  NOT NULL COMMENT '用户账号',
    `user_ip`         varchar(50)  NOT NULL COMMENT '用户 IP',
    `user_agent`      varchar(255) NOT NULL COMMENT '浏览器 UA',
    `creator`         varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户在线 Session';

DROP TABLE IF EXISTS `system_login_log`;
CREATE TABLE `system_login_log`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `log_type`    varchar(64)  NOT NULL COMMENT '日志类型',
    `trace_id`    varchar(64)  NOT NULL DEFAULT '' COMMENT '链路追踪编号',
    `user_id`     bigint       NOT NULL DEFAULT 0 COMMENT '用户编号',
    `user_type`   varchar(64)  NOT NULL DEFAULT 0 COMMENT '用户类型',
    `username`    varchar(50)  NOT NULL DEFAULT '' COMMENT '用户账号',
    `result`      varchar(64)  NOT NULL COMMENT '登陆结果',
    `user_ip`     varchar(50)  NOT NULL COMMENT '用户 IP',
    `user_agent`  varchar(255) NOT NULL COMMENT '浏览器 UA',
    `creator`     varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2087
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录';


-- system social -------------
DROP TABLE IF EXISTS `system_social_user`;
CREATE TABLE `system_social_user`
(
    `id`             bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
    `type`           varchar(64)     NOT NULL COMMENT '社交平台的类型',
    `openid`         varchar(32)     NOT NULL COMMENT '社交 openid',
    `token`          varchar(256)    NULL     DEFAULT NULL COMMENT '社交 token',
    `raw_token_info` text   NOT NULL COMMENT '原始 Token 数据，一般是 JSON 格式',
    `nickname`       varchar(32)     NOT NULL COMMENT '用户昵称',
    `avatar`         varchar(255)    NULL     DEFAULT NULL COMMENT '用户头像',
    `raw_user_info`  text   NOT NULL COMMENT '原始用户数据，一般是 JSON 格式',
    `code`           varchar(256)    NOT NULL COMMENT '最后一次的认证 code',
    `state`          varchar(16)     NULL     DEFAULT NULL COMMENT '最后一次的认证 state',
    `creator`        varchar(64)     NULL     DEFAULT '' COMMENT '创建者',
    `create_time`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        varchar(64)     NULL     DEFAULT '' COMMENT '更新者',
    `update_time`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '社交用户表';



DROP TABLE IF EXISTS `system_social_user_bind`;
CREATE TABLE `system_social_user_bind`
(
    `id`             bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键(自增策略)',
    `user_id`        bigint          NOT NULL COMMENT '用户编号',
    `user_type`      varchar(16)     NOT NULL COMMENT '用户类型',
    `social_type`    varchar(64)     NOT NULL COMMENT '社交平台的类型',
    `social_user_id` bigint          NOT NULL COMMENT '社交用户的编号',
    `creator`        varchar(64)     NULL     DEFAULT '' COMMENT '创建者',
    `create_time`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        varchar(64)     NULL     DEFAULT '' COMMENT '更新者',
    `update_time`    datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        bit(1)          NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 39
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '社交绑定表';


-- system oauth ----------------
DROP TABLE IF EXISTS `system_oauth2_access_token`;
CREATE TABLE `system_oauth2_access_token`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`       bigint       NOT NULL COMMENT '用户编号',
    `user_type`     varchar(16)  NOT NULL COMMENT '用户类型',
    `access_token`  varchar(255) NOT NULL COMMENT '访问令牌',
    `refresh_token` varchar(32)  NOT NULL COMMENT '刷新令牌',
    `client_id`     varchar(255) NOT NULL COMMENT '客户端编号',
    `scopes`        varchar(255) NULL     DEFAULT NULL COMMENT '授权范围',
    `expires_time`  datetime     NOT NULL COMMENT '过期时间',
    `creator`       varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1432
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 访问令牌';

DROP TABLE IF EXISTS `system_oauth2_approve`;
CREATE TABLE `system_oauth2_approve`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`      bigint       NOT NULL COMMENT '用户编号',
    `user_type`    varchar(16)  NOT NULL COMMENT '用户类型',
    `client_id`    varchar(255) NOT NULL COMMENT '客户端编号',
    `scope`        varchar(255) NOT NULL DEFAULT '' COMMENT '授权范围',
    `approved`     bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否接受',
    `expires_time` datetime     NOT NULL COMMENT '过期时间',
    `creator`      varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 82
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 批准表';

DROP TABLE IF EXISTS `system_oauth2_client`;
CREATE TABLE `system_oauth2_client`
(
    `id`                             bigint        NOT NULL AUTO_INCREMENT COMMENT '编号',
    `client_id`                      varchar(255)  NOT NULL COMMENT '客户端编号',
    `secret`                         varchar(255)  NOT NULL COMMENT '客户端密钥',
    `name`                           varchar(255)  NOT NULL COMMENT '应用名',
    `logo`                           varchar(255)  NOT NULL COMMENT '应用图标',
    `description`                    varchar(255)  NULL     DEFAULT NULL COMMENT '应用描述',
    `state`                          varchar(16)   NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
    `access_token_validity_seconds`  int           NOT NULL COMMENT '访问令牌的有效期',
    `refresh_token_validity_seconds` int           NOT NULL COMMENT '刷新令牌的有效期',
    `redirect_uris`                  varchar(255)  NOT NULL COMMENT '可重定向的 URI 地址',
    `authorized_grant_types`         varchar(255)  NOT NULL COMMENT '授权类型',
    `scopes`                         varchar(255)  NULL     DEFAULT NULL COMMENT '授权范围',
    `auto_approve_scopes`            varchar(255)  NULL     DEFAULT NULL COMMENT '自动通过的授权范围',
    `authorities`                    varchar(255)  NULL     DEFAULT NULL COMMENT '权限',
    `resource_ids`                   varchar(255)  NULL     DEFAULT NULL COMMENT '资源',
    `additional_information`         text NULL     DEFAULT NULL COMMENT '附加信息',
    `creator`                        varchar(64)   NULL     DEFAULT '' COMMENT '创建者',
    `create_time`                    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`                        varchar(64)   NULL     DEFAULT '' COMMENT '更新者',
    `update_time`                    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`                        bit(1)        NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 43
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 客户端表';


BEGIN;
INSERT INTO `system_oauth2_client` (`id`, `client_id`, `secret`, `name`, `logo`, `description`, `state`,
                                    `access_token_validity_seconds`, `refresh_token_validity_seconds`, `redirect_uris`,
                                    `authorized_grant_types`, `scopes`, `auto_approve_scopes`, `authorities`,
                                    `resource_ids`, `additional_information`, `creator`, `create_time`, `updater`,
                                    `update_time`, `deleted`)
VALUES (1, 'default', 'admin123', 'admin', 'http://test.yudao.iocoder.cn/a5e2e244368878a366b516805a4aabf1.png',
        '我是描述', 'ENABLE', 1800, 43200, '[\"https://www.iocoder.cn\",\"https://doc.iocoder.cn\"]',
        '[\"password\",\"authorization_code\",\"implicit\",\"refresh_token\"]', '[\"user.read\",\"user.write\"]', '[]',
        '[\"user.read\",\"user.write\"]', '[]', '{}', '1', '2022-05-11 21:47:12', '1', '2022-07-05 16:23:52', b'0');
INSERT INTO `system_oauth2_client` (`id`, `client_id`, `secret`, `name`, `logo`, `description`, `state`,
                                    `access_token_validity_seconds`, `refresh_token_validity_seconds`, `redirect_uris`,
                                    `authorized_grant_types`, `scopes`, `auto_approve_scopes`, `authorities`,
                                    `resource_ids`, `additional_information`, `creator`, `create_time`, `updater`,
                                    `update_time`, `deleted`)
VALUES (42, 'yudao-sso-demo-by-password', 'test', '基于密码模式，如何实现 SSO 单点登录？',
        'http://test.yudao.iocoder.cn/604bdc695e13b3b22745be704d1f2aa8ee05c5f26f9fead6d1ca49005afbc857.jpeg', NULL, 'ENABLE',
        1800, 43200, '[\"http://127.0.0.1:18080\"]', '[\"password\",\"refresh_token\"]',
        '[\"user.read\",\"user.write\"]', '[]', '[]', '[]', NULL, '1', '2022-10-04 17:40:16', '1',
        '2022-10-04 20:31:21', b'0');
COMMIT;

DROP TABLE IF EXISTS `system_oauth2_code`;
CREATE TABLE `system_oauth2_code`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`      bigint       NOT NULL COMMENT '用户编号',
    `user_type`    varchar(16)  NOT NULL COMMENT '用户类型',
    `code`         varchar(32)  NOT NULL COMMENT '授权码',
    `client_id`    varchar(255) NOT NULL COMMENT '客户端编号',
    `scopes`       varchar(255) NULL     DEFAULT '' COMMENT '授权范围',
    `expires_time` datetime     NOT NULL COMMENT '过期时间',
    `redirect_uri` varchar(255) NULL     DEFAULT NULL COMMENT '可重定向的 URI 地址',
    `state`        varchar(16)  NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
    `creator`      varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 143
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 授权码表';

DROP TABLE IF EXISTS `system_oauth2_refresh_token`;
CREATE TABLE `system_oauth2_refresh_token`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`       bigint       NOT NULL COMMENT '用户编号',
    `refresh_token` varchar(32)  NOT NULL COMMENT '刷新令牌',
    `user_type`     varchar(16)  NOT NULL COMMENT '用户类型',
    `client_id`     varchar(255) NOT NULL COMMENT '客户端编号',
    `scopes`        varchar(255) NULL     DEFAULT NULL COMMENT '授权范围',
    `expires_time`  datetime     NOT NULL COMMENT '过期时间',
    `creator`       varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 668
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 刷新令牌';



DROP TABLE IF EXISTS `system_error_code`;
CREATE TABLE `system_error_code`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '错误码编号',
    `type`             varchar(64)  NOT NULL DEFAULT 0 COMMENT '错误码类型',
    `application_name` varchar(50)  NOT NULL COMMENT '应用名',
    `code`             int          NOT NULL DEFAULT 0 COMMENT '错误码编码',
    `message`          varchar(255) NOT NULL DEFAULT '' COMMENT '错误码错误提示',
    `memo`             varchar(255) NULL     DEFAULT '' COMMENT '备注',
    `creator`          varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5833
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '错误码表';


DROP TABLE IF EXISTS `system_operate_log`;
CREATE TABLE `system_operate_log`
(
    `id`               bigint        NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `trace_id`         varchar(64)   NOT NULL DEFAULT '' COMMENT '链路追踪编号',
    `user_id`          bigint        NOT NULL COMMENT '用户编号',
    `user_type`        varchar(16)   NOT NULL DEFAULT 0 COMMENT '用户类型',
    `module`           varchar(50)   NOT NULL COMMENT '模块标题',
    `name`             varchar(50)   NOT NULL COMMENT '操作名',
    `type`             bigint        NOT NULL DEFAULT 0 COMMENT '操作分类',
    `content`          text NOT NULL DEFAULT '' COMMENT '操作内容',
    `exts`             varchar(255)  NOT NULL DEFAULT '' COMMENT '拓展字段',
    `request_method`   varchar(16)   NULL     DEFAULT '' COMMENT '请求方法名',
    `request_url`      varchar(255)  NULL     DEFAULT '' COMMENT '请求地址',
    `user_ip`          varchar(50)   NULL     DEFAULT NULL COMMENT '用户 IP',
    `user_agent`       varchar(255)  NULL     DEFAULT NULL COMMENT '浏览器 UA',
    `java_method`      varchar(255)  NOT NULL DEFAULT '' COMMENT 'Java 方法名',
    `java_method_args` text NULL     DEFAULT '' COMMENT 'Java 方法的参数',
    `start_time`       datetime      NOT NULL COMMENT '操作时间',
    `duration`         int           NOT NULL COMMENT '执行时长',
    `result_code`      int           NOT NULL DEFAULT 0 COMMENT '结果码',
    `result_msg`       varchar(255)  NULL     DEFAULT '' COMMENT '结果提示',
    `result_data`      text NULL     DEFAULT '' COMMENT '结果数据',
    `creator`          varchar(64)   NULL     DEFAULT '' COMMENT '创建者',
    `create_time`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          varchar(64)   NULL     DEFAULT '' COMMENT '更新者',
    `update_time`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          bit(1)        NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5909
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录';


--  mail ------------
DROP TABLE IF EXISTS `system_mail_account`;
CREATE TABLE `system_mail_account`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `mail`        varchar(128) NOT NULL COMMENT '邮箱',
    `username`    varchar(128) NOT NULL COMMENT '用户名',
    `password`    varchar(128) NOT NULL COMMENT '密码',
    `host`        varchar(64)  NOT NULL COMMENT 'SMTP 服务器域名',
    `port`        int          NOT NULL COMMENT 'SMTP 服务器端口',
    `ssl_enable`  bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否开启 SSL',
    `creator`     varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '邮箱账号表';


DROP TABLE IF EXISTS `system_mail_log`;
CREATE TABLE `system_mail_log`
(
    `id`                bigint         NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id`           bigint         NULL     DEFAULT NULL COMMENT '用户编号',
    `user_type`         varchar(16)    NULL     DEFAULT NULL COMMENT '用户类型',
    `to_mail`           varchar(128)   NOT NULL COMMENT '接收邮箱地址',
    `account_id`        bigint         NOT NULL COMMENT '邮箱账号编号',
    `from_mail`         varchar(128)   NOT NULL COMMENT '发送邮箱地址',
    `template_id`       bigint         NOT NULL COMMENT '模板编号',
    `template_code`     varchar(63)    NOT NULL COMMENT '模板编码',
    `template_nickname` varchar(128)   NULL     DEFAULT NULL COMMENT '模版发送人名称',
    `template_title`    varchar(255)   NOT NULL COMMENT '邮件标题',
    `template_content`  text NOT NULL COMMENT '邮件内容',
    `template_params`   varchar(255)   NOT NULL COMMENT '邮件参数',
    `send_state`        varchar(64)    NOT NULL DEFAULT 0 COMMENT '发送状态',
    `send_time`         datetime       NULL     DEFAULT NULL COMMENT '发送时间',
    `send_message_id`   varchar(255)   NULL     DEFAULT NULL COMMENT '发送返回的消息 ID',
    `send_exception`    text  NULL     DEFAULT NULL COMMENT '发送异常',
    `creator`           varchar(64)    NULL     DEFAULT '' COMMENT '创建者',
    `create_time`       datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`           varchar(64)    NULL     DEFAULT '' COMMENT '更新者',
    `update_time`       datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           bit(1)         NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 354
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '邮件日志表';



DROP TABLE IF EXISTS `system_mail_template`;
CREATE TABLE `system_mail_template`
(
    `id`          bigint         NOT NULL AUTO_INCREMENT COMMENT '编号',
    `name`        varchar(63)    NOT NULL COMMENT '模板名称',
    `code`        varchar(63)    NOT NULL COMMENT '模板编码',
    `account_id`  bigint         NOT NULL COMMENT '发送的邮箱账号编号',
    `nickname`    varchar(255)   NULL     DEFAULT NULL COMMENT '发送人名称',
    `title`       varchar(255)   NOT NULL COMMENT '模板标题',
    `content`     text NOT NULL COMMENT '模板内容',
    `params`      varchar(255)   NOT NULL COMMENT '参数数组',
    `state`       varchar(16)    NOT NULL COMMENT '开启状态',
    `remark`      varchar(255)   NULL     DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)    NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)    NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)         NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '邮件模版表';


-- notice --------------
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title`       varchar(50) NOT NULL COMMENT '公告标题',
    `content`     text        NOT NULL COMMENT '公告内容',
    `type`        varchar(64) NOT NULL COMMENT '公告类型（1通知 2公告）',
    `state`       varchar(16) NOT NULL DEFAULT 'ENABLE' COMMENT '公告状态',
    `creator`     varchar(64) NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64) NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)      NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表';


DROP TABLE IF EXISTS `system_notify_message`;
CREATE TABLE `system_notify_message`
(
    `id`                bigint        NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `user_id`           bigint        NOT NULL COMMENT '用户id',
    `user_type`         varchar(16)   NOT NULL COMMENT '用户类型',
    `template_id`       bigint        NOT NULL COMMENT '模版编号',
    `template_code`     varchar(64)   NOT NULL COMMENT '模板编码',
    `template_nickname` varchar(64)   NOT NULL COMMENT '模版发送人名称',
    `template_content`  text NOT NULL COMMENT '模版内容',
    `template_type`     int           NOT NULL COMMENT '模版类型',
    `template_params`   varchar(255)  NOT NULL COMMENT '模版参数',
    `read_state`        bit(1)        NOT NULL COMMENT '是否已读',
    `read_time`         datetime      NULL     DEFAULT NULL COMMENT '阅读时间',
    `creator`           varchar(64)   NULL     DEFAULT '' COMMENT '创建者',
    `create_time`       datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`           varchar(64)   NULL     DEFAULT '' COMMENT '更新者',
    `update_time`       datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           bit(1)        NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 9
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '站内信消息表';


DROP TABLE IF EXISTS `system_notify_template`;
CREATE TABLE `system_notify_template`
(
    `id`          bigint        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        varchar(63)   NOT NULL COMMENT '模板名称',
    `code`        varchar(64)   NOT NULL COMMENT '模版编码',
    `nickname`    varchar(255)  NOT NULL COMMENT '发送人名称',
    `content`     text NOT NULL COMMENT '模版内容',
    `type`        varchar(64)   NOT NULL COMMENT '类型',
    `params`      varchar(255)  NULL     DEFAULT NULL COMMENT '参数数组',
    `state`       varchar(16)   NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
    `remark`      varchar(255)  NULL     DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)   NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)   NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)        NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '站内信模板表';


-- sms -------
DROP TABLE IF EXISTS `system_sms_channel`;
CREATE TABLE `system_sms_channel`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `signature`    varchar(12)  NOT NULL COMMENT '短信签名',
    `code`         varchar(63)  NOT NULL COMMENT '渠道编码',
    `state`        varchar(16)  NOT NULL DEFAULT 'ENABLE' COMMENT '开启状态',
    `remark`       varchar(255) NULL     DEFAULT NULL COMMENT '备注',
    `api_key`      varchar(128) NOT NULL COMMENT '短信 API 的账号',
    `api_secret`   varchar(128) NULL     DEFAULT NULL COMMENT '短信 API 的秘钥',
    `callback_url` varchar(255) NULL     DEFAULT NULL COMMENT '短信发送回调 URL',
    `creator`      varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '短信渠道';


DROP TABLE IF EXISTS `system_sms_code`;
CREATE TABLE `system_sms_code`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `mobile`      varchar(11)  NOT NULL COMMENT '手机号',
    `code`        varchar(6)   NOT NULL COMMENT '验证码',
    `create_ip`   varchar(15)  NOT NULL COMMENT '创建 IP',
    `scene`       varchar(64)  NOT NULL COMMENT '发送场景',
    `today_index` tinyint      NOT NULL COMMENT '今日发送的第几条',
    `used`        tinyint      NOT NULL COMMENT '是否使用',
    `used_time`   datetime     NULL     DEFAULT NULL COMMENT '使用时间',
    `used_ip`     varchar(255) NULL     DEFAULT NULL COMMENT '使用 IP',
    `creator`     varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_mobile` (`mobile` ASC) USING BTREE COMMENT '手机号'
) ENGINE = InnoDB
  AUTO_INCREMENT = 484
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '手机验证码';



DROP TABLE IF EXISTS `system_sms_log`;
CREATE TABLE `system_sms_log`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `channel_id`       bigint       NOT NULL COMMENT '短信渠道编号',
    `channel_code`     varchar(63)  NOT NULL COMMENT '短信渠道编码',
    `template_id`      bigint       NOT NULL COMMENT '模板编号',
    `template_code`    varchar(63)  NOT NULL COMMENT '模板编码',
    `template_type`    varchar(64)  NOT NULL COMMENT '短信类型',
    `template_content` varchar(255) NOT NULL COMMENT '短信内容',
    `template_params`  varchar(255) NOT NULL COMMENT '短信参数',
    `api_template_id`  varchar(63)  NOT NULL COMMENT '短信 API 的模板编号',
    `mobile`           varchar(11)  NOT NULL COMMENT '手机号',
    `user_id`          bigint       NULL     DEFAULT NULL COMMENT '用户编号',
    `user_type`        varchar(16)  NULL     DEFAULT NULL COMMENT '用户类型',
    `send_state`       varchar(64)  NOT NULL DEFAULT 0 COMMENT '发送状态',
    `send_time`        datetime     NULL     DEFAULT NULL COMMENT '发送时间',
    `send_code`        int          NULL     DEFAULT NULL COMMENT '发送结果的编码',
    `send_msg`         varchar(255) NULL     DEFAULT NULL COMMENT '发送结果的提示',
    `api_send_code`    varchar(63)  NULL     DEFAULT NULL COMMENT '短信 API 发送结果的编码',
    `api_send_msg`     varchar(255) NULL     DEFAULT NULL COMMENT '短信 API 发送失败的提示',
    `api_request_id`   varchar(255) NULL     DEFAULT NULL COMMENT '短信 API 发送返回的唯一请求 ID',
    `api_serial_no`    varchar(255) NULL     DEFAULT NULL COMMENT '短信 API 发送返回的序号',
    `receive_state`    varchar(64)  NOT NULL DEFAULT 0 COMMENT '接收状态',
    `receive_time`     datetime     NULL     DEFAULT NULL COMMENT '接收时间',
    `api_receive_code` varchar(63)  NULL     DEFAULT NULL COMMENT 'API 接收结果的编码',
    `api_receive_msg`  varchar(255) NULL     DEFAULT NULL COMMENT 'API 接收结果的说明',
    `creator`          varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 348
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '短信日志';



DROP TABLE IF EXISTS `system_sms_template`;
CREATE TABLE `system_sms_template`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT COMMENT '编号',
    `type`            varchar(64)  NOT NULL COMMENT '短信签名',
    `state`           varchar(16)  NOT NULL DEFAULT 'ENABLE' COMMENT '开启状态',
    `code`            varchar(63)  NOT NULL COMMENT '模板编码',
    `name`            varchar(63)  NOT NULL COMMENT '模板名称',
    `content`         varchar(255) NOT NULL COMMENT '模板内容',
    `params`          varchar(255) NOT NULL COMMENT '参数数组',
    `remark`          varchar(255) NULL     DEFAULT NULL COMMENT '备注',
    `api_template_id` varchar(63)  NOT NULL COMMENT '短信 API 的模板编号',
    `channel_id`      bigint       NOT NULL COMMENT '短信渠道编号',
    `channel_code`    varchar(63)  NOT NULL COMMENT '短信渠道编码',
    `creator`         varchar(64)  NULL     DEFAULT '' COMMENT '创建者',
    `create_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`         varchar(64)  NULL     DEFAULT '' COMMENT '更新者',
    `update_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 14
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '短信模板';
