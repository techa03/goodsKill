CREATE database seckill;
use seckill;

CREATE TABLE seckill(
`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '??????id',
`name` varchar(120) not null comment '???????',
`number` int not null comment '???????',
`start_time` timestamp not null comment '??????????',
`start_time` timestamp not null comment '??????????',
`end_time` timestamp not null default current_timestamp comment '???????',
primary key(seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='???????';

insert into seckill(name,number,start_time,end_time)
values
	('1000????iphone6',100,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
	('500????ipad2',100,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
	('300????С??4',100,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
	('200????????note',100,'2015-11-01 00:00:00','2015-11-02 00:00:00');
	
create table success_killed(
'seckill_id' bigint not null comment '??????',
'user_phone' bigint not null comment '????????',
'state' tinyint not null default -1 comment '???????-1????Ч   0?????   1???????',
'create_time' timestamp not null comment '???????',
primary key(seckill_id,user_phone),
key idx_create_time(create_time)
)ENGINE=InnoDB default charset=utf8 comment='???????????';
