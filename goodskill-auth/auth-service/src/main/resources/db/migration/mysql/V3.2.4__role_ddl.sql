alter table permission
    add permission_code varchar(32) default '' not null;

UPDATE seckill.permission t SET t.permission_code = 'user:manage' WHERE t.permission_id = 3;
UPDATE seckill.permission t SET t.permission_code = 'role:permission:manage' WHERE t.permission_id = 6;
UPDATE seckill.permission t SET t.permission_code = 'dict:manage' WHERE t.permission_id = 11;
UPDATE seckill.permission t SET t.permission_code = 'menu:manage' WHERE t.permission_id = 5;
UPDATE seckill.permission t SET t.permission_code = 'user:role:manage' WHERE t.permission_id = 4;
UPDATE seckill.permission t SET t.permission_code = 'biz:manage' WHERE t.permission_id = 10;
UPDATE seckill.permission t SET t.permission_code = 'system:manage' WHERE t.permission_id = 7;

UPDATE seckill.role SET role_code = 'system-manager' WHERE role_id = 1;
UPDATE seckill.role SET role_code = 'storage-operator' WHERE role_id = 2;
UPDATE seckill.role SET role_code = 'goods-manager' WHERE role_id = 7;
UPDATE seckill.role SET role_code = 'seckill-manager' WHERE role_id = 8;
