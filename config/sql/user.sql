create database if not exists istar;
use istar;
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`           bigint                                                  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uuid`         varchar(64)                                             NOT NULL comment '用户识别码',
    `gender`       tinyint(1)                                              NULL DEFAULT NULL comment '性别0女1男',
    `nick_name`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL comment '昵称',
    `mobile`       varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL comment '手机号',
    `username`     varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL comment '用户名',
    `email`        varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL comment '邮箱',
    `password`     varchar(256)                                            NOT NULL comment '密码',
    `birthday`     date                                                    NULL DEFAULT NULL comment '生日',
    `avatar_url`   varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL comment '头像地址',
    `openid`       char(28) CHARACTER SET utf8 COLLATE utf8_general_ci     NULL DEFAULT NULL comment '开放openid',
    `session_key`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL comment '开放session_key',
    `status`       tinyint(1)                                              NULL DEFAULT 0 comment '用户状态0正常-1禁用',
    `point`        bigint                                                  NULL DEFAULT 0 COMMENT '会员积分',
    `deleted`      tinyint(1)                                              NULL DEFAULT 0 comment '是否删除',
    `gmt_create`   bigint                                                  NULL DEFAULT NULL comment '创建时间',
    `gmt_modified` bigint                                                  NULL DEFAULT NULL comment '修改时间',
    `balance`      double                                                  NULL DEFAULT 0 COMMENT '用户余额',
    `city`         varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL comment '城市',
    `country`      varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL comment '国家',
    `language`     varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL comment '语言',
    `province`     varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL comment '省份',
    `roles`        varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT 'role_common_user' comment '角色权限--role_sys_superAdmin,role_sys_admin,role_common_user',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uuid` (`uuid`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `mobile` (`mobile`),
    UNIQUE KEY `email` (`email`)

) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = DYNAMIC;
drop table if exists t_video;
create table t_video
(
    `id`                          bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uuid`                        varchar(64)  NOT NULL comment '视频所属用户',
    `video_id`                    varchar(64)  NOT NULL COMMENT '视频id',
    `video_hash`                  varchar(64)  NOT NULL COMMENT '视频hash',
    `video_url`                   varchar(255) NOT NULL COMMENT '视频url',
    `video_title`                 varchar(255) NOT NULL COMMENT '视频标题',
    `video_cover`                 varchar(255) NULL DEFAULT NULL COMMENT '视频封面',
    `video_desc`                  varchar(255) NULL DEFAULT NULL COMMENT '视频描述',
    `video_duration`              int          NULL DEFAULT 0 COMMENT '视频时长',
    `video_size`                  int          NULL DEFAULT 0 COMMENT '视频大小单位:字节',
    `video_status`                tinyint      NULL DEFAULT 0 COMMENT '视频状态0正常,-1禁止',
    `video_from`                  tinyint      NULL DEFAULT 0 COMMENT '视频来源',
    `video_forbidden_reason`      varchar(255) NULL DEFAULT NULL COMMENT '视频禁止原因',
    `video_forbidden_reason_time` bigint       NULL DEFAULT NULL COMMENT '视频禁止原因时间',
    `video_price`                 double       NULL DEFAULT 0 COMMENT '视频价格',
    `video_views`                 int          NULL DEFAULT 0 COMMENT '视频浏览量',
    `video_likes`                 int          NULL DEFAULT 0 COMMENT '视频点赞量',
    `video_comments`              int          NULL DEFAULT 0 COMMENT '视频评论量',
    `video_favorites`             int          NULL DEFAULT 0 COMMENT '视频收藏量',
    `video_purchase`              int          NULL DEFAULT 0 COMMENT '视频购买人数',
    `video_tags`                  varchar(255) NULL DEFAULT NULL COMMENT '视频标签',
    `video_type`                  tinyint      NULL DEFAULT NULL COMMENT '视频类型',
    `video_create_time`           bigint       NOT NULL COMMENT '视频创建时间',
    `video_update_time`           bigint       NOT NULL COMMENT '视频更新时间',
    `video_delete_time`           bigint       NOT NULL COMMENT '视频删除时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `video_id` (`video_id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = DYNAMIC;

insert into t_video(uuid, video_id, video_hash, video_url, video_title, video_create_time, video_update_time,
                    video_delete_time)
values ('uuid-001', 'video-id-001', 'video_hash-001', 'video_url-001', 'video_title-001', 1500000000, 1500000000,
        1500000000);
insert into t_video(uuid, video_id, video_hash, video_url, video_title, video_create_time, video_update_time,
                    video_delete_time)
values ('uuid-002', 'video-id-002', 'video_hash-001', 'video_url-001', 'video_title-001', 1500000000, 1500000000,
        1500000000);
insert into t_video(uuid, video_id, video_hash, video_url, video_title, video_create_time, video_update_time,
                    video_delete_time)
values ('uuid-001', 'video-id-003', 'video_hash-001', 'video_url-001', 'video_title-001', 1500000000, 1500000000,
        1500000000);
insert into t_video(uuid, video_id, video_hash, video_url, video_title, video_create_time, video_update_time,
                    video_delete_time)
values ('uuid-001', 'video-id-004', 'video_hash-001', 'video_url-001', 'video_title-001', 1500000000, 1500000000,
        1500000000);
insert into t_video(uuid, video_id, video_hash, video_url, video_title, video_create_time, video_update_time,
                    video_delete_time, video_likes)
values ('uuid-001', 'video-id-005', 'video_hash-001', 'video_url-001', 'video_title-001', 1500000000, 1500000000,
        1500000000, 100);
insert into t_video(uuid, video_id, video_hash, video_url, video_title, video_create_time, video_update_time,
                    video_delete_time, video_likes)
values ('uuid-001', 'video-id-006', 'video_hash-001', 'video_url-001', 'video_title-001', 1500000000, 1500000000,
        1500000000, 20);


