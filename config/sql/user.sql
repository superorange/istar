drop database if exists istar;
create database if not exists istar;
use istar;
DROP TABLE IF EXISTS `t_user`;
drop table if exists `t_topic`;
drop table if exists `t_comment`;
drop table if exists `t_reply`;
drop table if exists `t_picture`;
drop table if exists `t_video`;

CREATE TABLE if not exists `t_user`
(
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uuid`        varchar(64)  NOT NULL comment '用户识别码',
    `gender`      tinyint      NULL     DEFAULT NULL comment '性别0女1男',
    `nick_name`   varchar(64)  NULL     DEFAULT NULL comment '昵称',
    `signature`   varchar(128) NULL     DEFAULT NULL comment '个性签名',
    `mobile`      varchar(64)  NULL     DEFAULT NULL comment '手机号',
    `username`    varchar(128) NULL     DEFAULT NULL comment '用户名',
    `email`       varchar(128) NULL     DEFAULT NULL comment '邮箱',
    `password`    varchar(256) NULL     DEFAULT NULL comment '密码',
    `birthday`    date         NULL     DEFAULT NULL comment '生日',
    `avatar`      varchar(256) NULL     DEFAULT NULL comment '头像地址',
    `openid`      char(28)     NULL     DEFAULT NULL comment '开放openid',
    `status`      tinyint      NOT NULL DEFAULT 0 comment '用户状态0正常-1禁用,1删除',
    `point`       bigint       NOT NULL DEFAULT 0 COMMENT '会员积分',
    `create_time` bigint       NULL     DEFAULT NULL comment '创建时间',
    `modify_time` bigint       NULL     DEFAULT NULL comment '修改时间',
    `balance`     double       NOT NULL DEFAULT 0.0 COMMENT '用户余额',
    `roles`       varchar(32)  NULL     DEFAULT 'role_sys_user' comment '角色权限--role_sys_superAdmin,role_sys_admin,role_sys_user',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uuid` (`uuid`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `mobile` (`mobile`),
    UNIQUE KEY `email` (`email`)
) ENGINE = InnoDB
  CHARACTER SET = UTF8MB4
  COLLATE = UTF8MB4_GENERAL_CI
  ROW_FORMAT = DYNAMIC;


#用户帖子表
create table if not exists `t_topic`
(
    `id`              bigint      not null auto_increment comment '主键',
    `uuid`            varchar(64) not null comment '用户识别码',
    `topic_id`        varchar(64) not null comment '文章id',
    `picture_id_list` text        null     default null comment '图片ID列表，用分号分隔',
    `video_id_list`   text        null     default null comment '视频ID列表，用分号分隔',
    `title`           text        null     default null comment '标题',
    `content`         text        null     default null comment '内容',
    `status`          tinyint     not null default 0 comment '状态0正常,-1禁用,1删除',
    `like_count`      int         not null default 0 comment '点赞数',
    `create_time`     bigint      not null comment '创建时间',
    `modify_time`     bigint      not null comment '修改时间',
    primary key (`id`),
    unique key `topic_id` (`topic_id`),
    index `uuid` (`uuid`)
) engine = innodb
  default charset = UTF8MB4
  collate = UTF8MB4_GENERAL_CI;


create table if not exists `t_comment`
(
    `id`          bigint      not null auto_increment comment '主键',
    `uuid`        varchar(64) not null comment '评论用户ID',
    `topic_id`    varchar(64) not null comment '评论主题ID',
    `comment_id`  varchar(64) not null comment '评论ID',
    `content`     text        not null comment '评论内容',
    `status`      tinyint     not null default 0 comment '状态0正常,-1禁用,1删除',
    `create_time` bigint      not null comment '创建时间',
    `like_count`  int         not null default 0 comment '点赞数',
    primary key (`id`),
    index `topic_id` (`topic_id`),
    index `uuid` (`uuid`),
    index `comment_id` (`comment_id`)
) engine = innodb
  default charset = UTF8MB4
  collate UTF8MB4_GENERAL_CI comment '主题评论表';

create table if not exists `t_reply`
(
    `id`            bigint      not null auto_increment comment '主键',
    `uuid`          varchar(64) not null comment '评论用户ID',
    `comment_id`    varchar(64) not null comment '被回复评论的ID',
    `reply_id`      varchar(64) not null comment '回复评论ID',
    `to_reply_id`   varchar(64) not null comment '被回复的回复评论ID',
    `to_reply_uuid` varchar(64) not null comment '被回复的回复用户ID',
    `content`       text        not null comment '回复内容',
    `status`        tinyint     not null default 0 comment '状态0正常,-1禁用,1删除',
    `create_time`   bigint      not null comment '创建时间',
    `like_count`    int         not null default 0 comment '点赞数',
    primary key (`id`),
    index `comment_id` (`comment_id`),
    index `uuid` (`uuid`)
) engine = innodb
  default charset = UTF8MB4
  collate UTF8MB4_GENERAL_CI comment '回复表';



create table if not exists `t_picture`
(
    `id`          bigint      not null auto_increment comment '主键',
    `uuid`        varchar(64) null     default null comment '用户识别码,可以为空',
    `pic_id`      varchar(64) not null comment '图片id',
    `pic_type`    varchar(64) not null comment '文件类型',
    `status`      tinyint     not null default 0 comment '状态0正常，-1禁用,1删除',
    `create_time` bigint      not null comment '创建时间',
    primary key (`id`),
    unique key `pic_id` (`pic_id`),
    index `uuid` (`uuid`)
) engine = innodb
  default charset = UTF8MB4
  collate UTF8MB4_GENERAL_CI;

create table if not exists `t_video`
(
    `id`          bigint       not null auto_increment comment '主键',
    `uuid`        varchar(64)  null     default null comment '用户识别码,可以为空',
    `video_id`    varchar(64)  not null comment '视频id',
    `video_hash`  varchar(256) not null comment '视频hash',
    `video_url`   varchar(256) not null comment '视频地址',
    `video_name`  varchar(64)  not null comment '视频名',
    `video_size`  bigint       not null comment '视频大小,单位字节',
    `video_type`  varchar(64)  null     default null comment '视频类型',
    `status`      tinyint      not null default 0 comment '状态0正常，-1禁用,1删除',
    `create_time` bigint       not null comment '创建时间',
    primary key (`id`),
    unique key `video_id` (`video_id`),
    index `uuid` (`uuid`),
    index `video_hash` (`video_hash`)
)
    engine = innodb
    default charset = UTF8MB4
    collate UTF8MB4_GENERAL_CI;

