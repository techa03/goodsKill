create table  goods
(
	goods_id int
		primary key,
	photo_url varchar(200) ,
	name varchar(45) ,
	price varchar(45) ,
	create_time timestamp ,
	introduce varchar(500) ,
	photo_image blob
);

create table  permission
(
	permission_id int
		primary key,
	permission_name varchar(64) ,
	create_time time ,
	update_time timestamp default CURRENT_TIMESTAMP ,
	permission_menu varchar(500) ,
	parent_permission_id int ,
	is_dir varchar(1) ,
	order_no int
);

create table  role
(
	role_id int
		primary key,
	role_name varchar(64) ,
	create_time time ,
	update_time timestamp default CURRENT_TIMESTAMP
);

create table  role_permission
(
	id int
		primary key,
	permission_id int  ,
	role_id int  ,
	create_time timestamp ,
	update_time timestamp
);

create table  seckill
(
	seckill_id bigint
		primary key,
	name varchar(120)  ,
	number int ,
	start_time timestamp default CURRENT_TIMESTAMP  ,
	end_time timestamp default CURRENT_TIMESTAMP  ,
	create_time timestamp default CURRENT_TIMESTAMP  ,
	goods_id int ,
	price decimal(10,2) ,
	status varchar(5) ,
	create_user varchar(45)
);

create index idx_create_time
	on seckill (create_time);

create index idx_end_time
	on seckill (end_time);

create index idx_start_time
	on seckill (start_time);

create table  success_killed
(
	seckill_id bigint  ,
	user_phone varchar(20)  ,
	status int default -1,
	create_time timestamp default CURRENT_TIMESTAMP,
	server_ip varchar(200) ,
	user_ip varchar(200) ,
	user_id varchar(45) ,
	primary key (seckill_id, user_phone)
);

create table  user
(
	id int
		primary key,
	account varchar(45) ,
	password varchar(45) default 'aa123456' ,
	create_time timestamp default CURRENT_TIMESTAMP ,
	update_time timestamp default CURRENT_TIMESTAMP ,
	username varchar(50) ,
	locked varchar(1) default '0' ,
	constraint account_UNIQUE
		unique (account)
);

create table  user_role
(
	id int
		primary key,
	user_id int  ,
	role_id int  ,
	create_time timestamp ,
	update_time timestamp
);

