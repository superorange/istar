create table if not exists t_user
(
    id           bigint       not null
        primary key,
    uuid         varchar(64)  not null,
    gender       smallint,
    nick_name    varchar(64),
    mobile       varchar(64),
    username     varchar(128),
    email        varchar(128),
    password     varchar(256) not null,
    birthday     date,
    avatar_url   varchar(256),
    openid       char(28),
    session_key  varchar(32),
    status       smallint,
    point        bigint,
    deleted      smallint,
    gmt_create   bigint,
    gmt_modified bigint,
    balance      double precision,
    city         varchar(32),
    country      varchar(32),
    language     varchar(10),
    province     varchar(32),
    roles        varchar(32)
);

comment on column t_user.id is '主键';

comment on column t_user.uuid is '用户识别码';

comment on column t_user.gender is '性别0女1男';

comment on column t_user.nick_name is '昵称';

comment on column t_user.mobile is '手机号';

comment on column t_user.username is '用户名';

comment on column t_user.email is '邮箱';

comment on column t_user.password is '密码';

comment on column t_user.birthday is '生日';

comment on column t_user.avatar_url is '头像地址';

comment on column t_user.openid is '开放openid';

comment on column t_user.session_key is '开放session_key';

comment on column t_user.status is '用户状态0正常-1禁用';

comment on column t_user.point is '会员积分';

comment on column t_user.deleted is '是否删除';

comment on column t_user.gmt_create is '创建时间';

comment on column t_user.gmt_modified is '修改时间';

comment on column t_user.balance is '用户余额';

comment on column t_user.city is '城市';

comment on column t_user.country is '国家';

comment on column t_user.language is '语言';

comment on column t_user.province is '省份';

comment on column t_user.roles is '角色权限--role_sys_superAdmin,role_sys_admin,role_common_user';

alter table t_user
    owner to postgres;

create unique index if not exists uuid
    on t_user (uuid);

create unique index if not exists username
    on t_user (username);

create unique index if not exists mobile
    on t_user (mobile);

create unique index if not exists email
    on t_user (email);

