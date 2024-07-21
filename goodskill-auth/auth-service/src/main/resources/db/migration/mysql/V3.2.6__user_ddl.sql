alter table seckill.user
    add create_user varchar(50) default '' not null comment '创建人';

alter table seckill.user
    add update_user varchar(50) default '' not null comment '更新人';

alter table seckill.user
    add delete_flag tinyint unsigned default '0' not null comment '逻辑删除标识';

-- permission
alter table seckill.permission
    add create_user varchar(50) default '' not null comment '创建人';
alter table seckill.permission
    add update_user varchar(50) default '' not null comment '更新人';
-- role
alter table seckill.role
    add create_user varchar(50) default '' not null comment '创建人';
alter table seckill.role
    add update_user varchar(50) default '' not null comment '更新人';
-- role_permission
alter table seckill.role_permission
    add create_user varchar(50) default '' not null comment '创建人';
alter table seckill.role_permission
    add update_user varchar(50) default '' not null comment '更新人';
-- user_role
alter table seckill.user_role
    add create_user varchar(50) default '' not null comment '创建人';
alter table seckill.user_role
    add update_user varchar(50) default '' not null comment '更新人';
-- user_auth_account
alter table seckill.user_auth_account
    add create_user varchar(50) default '' not null comment '创建人';
alter table seckill.user_auth_account
    add update_user varchar(50) default '' not null comment '更新人';
