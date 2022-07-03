create database if not exists istar;
use istar;
DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user`
(
    `id`           bigint                                                  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uuid`         varchar(64)                                             NOT NULL comment '用户识别码',
    `gender`       tinyint(1)                                              NULL     DEFAULT NULL comment '性别0女1男',
    `nick_name`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL comment '昵称',
    `mobile`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL comment '手机号',
    `username`     varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL comment '用户名',
    `email`        varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL comment '邮箱',
    `password`     varchar(256)                                            NULL     DEFAULT NULL comment '密码',
    `birthday`     date                                                    NULL     DEFAULT NULL comment '生日',
    `avatar_url`   varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL comment '头像地址',
    `openid`       char(28) CHARACTER SET utf8 COLLATE utf8_general_ci     NULL     DEFAULT NULL comment '开放openid',
    `status`       tinyint(1)                                              NOT NULL DEFAULT 0 comment '用户状态0正常-1禁用,1删除',
    `point`        bigint                                                  NOT NULL DEFAULT 0 COMMENT '会员积分',
    `gmt_create`   bigint                                                  NULL     DEFAULT NULL comment '创建时间',
    `gmt_modified` bigint                                                  NULL     DEFAULT NULL comment '修改时间',
    `balance`      double                                                  NOT NULL DEFAULT 0.0 COMMENT '用户余额',
    `roles`        varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT 'role_sys_user' comment '角色权限--role_sys_superAdmin,role_sys_admin,role_sys_user',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uuid` (`uuid`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `mobile` (`mobile`),
    UNIQUE KEY `email` (`email`)
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = DYNAMIC;
#用户帖子表
create table if not exists `t_post`
(
    `id`           bigint      not null auto_increment comment '主键',
    `uuid`         varchar(64) not null comment '用户识别码',
    `post_id`      varchar(64) not null comment '帖子id',
    `pic_id`       text        null     default null comment '图片ID列表，用分号分隔',
    `video_id`     text        null     default null comment '视频ID列表，用分号分隔',
    `title`        text        null     default null comment '标题',
    `content`      text        null     default null comment '内容',
    `status`       tinyint(1)  not null default 0 comment '状态0正常,-1禁用,1删除',
    `gmt_create`   bigint      not null comment '创建时间',
    `gmt_modified` bigint      not null comment '修改时间',
    primary key (`id`),
    unique key `post_id` (`post_id`),
    index `uuid` (`uuid`)
) engine = innodb
  default charset = utf8
  collate utf8_general_ci;



create table if not exists `t_pictures`
(
    `id`         bigint       not null auto_increment comment '主键',
    `uuid`       varchar(64)  null     default null comment '用户识别码,可以为空',
    `pic_id`     varchar(64)  not null comment '图片id',
    `pic_url`    varchar(256) not null comment '图片地址',
    `pic_name`   varchar(64)  not null comment '文件名',
    `pic_type`   varchar(64)  not null comment '文件类型',
    `status`     tinyint(1)   not null default 0 comment '状态0正常，-1禁用,1删除',
    `gmt_create` bigint       not null comment '创建时间',
    primary key (`id`),
    unique key `pic_id` (`pic_id`),
    index `uuid` (`uuid`)
) engine = innodb
  default charset = utf8
  collate utf8_general_ci;

create table if not exists `t_videos`
(
    `id`         bigint       not null auto_increment comment '主键',
    `uuid`       varchar(64)  null     default null comment '用户识别码,可以为空',
    `video_id`   varchar(64)  not null comment '视频id',
    `video_hash` varchar(256) not null comment '视频hash',
    `video_url`  varchar(256) not null comment '视频地址',
    `video_name` varchar(64)  not null comment '视频名',
    `video_size` bigint       not null comment '视频大小,单位字节',
    `video_type` varchar(64)  null     default null comment '视频类型',
    `status`     tinyint(1)   not null default 0 comment '状态0正常，-1禁用,1删除',
    `gmt_create` bigint       not null comment '创建时间',
    primary key (`id`),
    unique key `video_id` (`video_id`),
    index `uuid` (`uuid`),
    index `video_hash` (`video_hash`)
)
    engine = innodb
    default charset = utf8
    collate utf8_general_ci;

