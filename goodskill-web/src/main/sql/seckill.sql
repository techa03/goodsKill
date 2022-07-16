/*
Navicat MySQL Data Transfer

Source Server         : seckillDocker
Source Server Version : 50722
Source Host           : 127.0.0.1:3306
Source Database       : seckill

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-08-19 22:50:02
*/
SET NAMES 'utf8';
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
create table goods
(
    goods_id    int auto_increment
        primary key,
    photo_url   varchar(200) null,
    name        varchar(45)  null,
    price       varchar(45)  null,
    create_time timestamp    null,
    introduce   varchar(500) null
)
    charset = utf8mb3;

INSERT INTO seckill.goods (goods_id, photo_url, name, price, create_time, introduce) VALUES (1, 'http://localhost:19000/goodskill/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20220511163056.png', 'iphone6', '4000', '2022-04-18 09:15:24', '似的发射点');
INSERT INTO seckill.goods (goods_id, photo_url, name, price, create_time, introduce) VALUES (2, 'http://localhost:19000/goodskill/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20220511162941.jpg', 'ipad2', '1900', null, null);
INSERT INTO seckill.goods (goods_id, photo_url, name, price, create_time, introduce) VALUES (3, '/Users/heng/java学习/小米4.jfif', '小米4', '800', null, null);
INSERT INTO seckill.goods (goods_id, photo_url, name, price, create_time, introduce) VALUES (4, '/Users/heng/java学习/红米note.jfif', '红米note', '699', null, null);
INSERT INTO seckill.goods (goods_id, photo_url, name, price, create_time, introduce) VALUES (5, null, '小米6', '2499', '2017-06-09 00:07:25', '小米6变焦双摄拍人更美');
INSERT INTO seckill.goods (goods_id, photo_url, name, price, create_time, introduce) VALUES (6, null, '小米max2', '1699', '2017-06-10 11:14:11', '6.44''''大屏 / 5300mAh 充电宝级的大电量 / 大像素相机 / 轻薄全金属 / ​4GB 大内存 / 骁龙八核处理器');
INSERT INTO seckill.goods (goods_id, photo_url, name, price, create_time, introduce) VALUES (7, null, '小米5c', '1499', '2017-06-10 11:16:20', '搭载澎湃S1 八核高性能处理器 / “暗夜之眼”超感光相机 / 135g 轻薄金属机身 / 5.15"高亮护眼屏');
INSERT INTO seckill.goods (goods_id, photo_url, name, price, create_time, introduce) VALUES (8, null, 'OPPO R11', '3499', '2017-06-10 11:23:43', '前后2000万，拍照更清晰');


-- ----------------------------
-- Table structure for permission
-- ----------------------------
create table permission
(
    permission_id        int unsigned auto_increment
        primary key,
    permission_name      varchar(64)                            null,
    create_time          datetime                               null,
    update_time          timestamp    default CURRENT_TIMESTAMP null,
    permission_menu      varchar(500)                           null,
    parent_permission_id int                                    null,
    is_dir               varchar(1)                             null,
    order_no             int          default 0                 not null,
    path                 varchar(200) default ''                not null,
    hidden               tinyint(1)   default 0                 null comment '是否隐藏：0-否，1-是',
    component            varchar(50)  default ''                not null,
    access_url           varchar(500) default ''                not null comment '访问url',
    redirect             varchar(100) default ''                not null
)
    collate = utf8_bin;

INSERT INTO seckill.permission (permission_id, permission_name, create_time, update_time, permission_menu, parent_permission_id, is_dir, order_no, path, hidden, component, access_url, redirect) VALUES (3, '用户管理', '2018-07-14 12:28:25', '2018-07-14 12:28:25', 'admin/user.html', 7, 'N', 1, 'user-manage', 0, 'views/table/user-manage', '', '');
INSERT INTO seckill.permission (permission_id, permission_name, create_time, update_time, permission_menu, parent_permission_id, is_dir, order_no, path, hidden, component, access_url, redirect) VALUES (4, '用户角色管理', '2018-07-14 12:28:25', '2018-07-14 12:28:25', 'admin/userRole.html', 7, 'N', 2, '', 1, '', '', '');
INSERT INTO seckill.permission (permission_id, permission_name, create_time, update_time, permission_menu, parent_permission_id, is_dir, order_no, path, hidden, component, access_url, redirect) VALUES (5, '菜单管理', '2018-07-14 12:28:25', '2018-07-14 12:28:25', 'admin/permission.html', 7, 'N', 3, '', 0, '', '', '');
INSERT INTO seckill.permission (permission_id, permission_name, create_time, update_time, permission_menu, parent_permission_id, is_dir, order_no, path, hidden, component, access_url, redirect) VALUES (6, '角色权限管理', '2018-07-14 12:28:25', '2018-07-14 12:28:25', 'admin/role.html', 7, 'N', 4, 'role-manage', 0, 'views/permission/role', '', '');
INSERT INTO seckill.permission (permission_id, permission_name, create_time, update_time, permission_menu, parent_permission_id, is_dir, order_no, path, hidden, component, access_url, redirect) VALUES (7, '系统管理', '2018-07-14 12:28:25', '2018-07-14 12:28:25', 'admin', null, 'Y', 0, '', 0, 'layout/Layout', '', '');
INSERT INTO seckill.permission (permission_id, permission_name, create_time, update_time, permission_menu, parent_permission_id, is_dir, order_no, path, hidden, component, access_url, redirect) VALUES (10, '业务管理', '2022-06-26 11:19:16', '2022-06-26 11:19:16', '/test', null, 'Y', 0, '', 0, 'layout/Layout', '', '');
INSERT INTO seckill.permission (permission_id, permission_name, create_time, update_time, permission_menu, parent_permission_id, is_dir, order_no, path, hidden, component, access_url, redirect) VALUES (11, '字典管理', '2022-06-26 11:23:26', '2022-06-26 11:23:26', '/test', null, 'Y', 0, 'dict-manage', 0, 'views/table/user-manage', '', '');


-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `role_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_name` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '管理员', '2018-07-14 12:29:38', '2018-07-14 12:29:28');
INSERT INTO `role` VALUES ('2', '库存操作员', '2018-07-14 12:29:38', '2018-07-14 12:29:38');
INSERT INTO `role` VALUES ('7', '商品管理员', '2018-07-15 20:29:27', '2018-07-15 20:29:27');
INSERT INTO `role` VALUES ('8', '秒杀管理员', '2018-07-15 20:29:27', '2018-07-15 20:29:27');

alter table role
    add role_code varchar(50) default '' not null comment '角色编码';
alter table role
    add status int null comment '角色状态';
alter table role
    add delete_flag tinyint default 0 not null comment '删除标识';
alter table role
    modify status tinyint default 0 null comment '角色状态';

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `permission_id` int(10) unsigned NOT NULL,
  `role_id` int(10) unsigned NOT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `for_permission_id` (`permission_id`),
  KEY `for_role_id` (`role_id`),
  CONSTRAINT `for_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `for_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES ('31', '7', '8', '2018-07-25 21:53:28', '2018-07-25 21:53:28');
INSERT INTO `role_permission` VALUES ('32', '3', '8', '2018-07-25 21:53:28', '2018-07-25 21:53:28');
INSERT INTO `role_permission` VALUES ('33', '4', '8', '2018-07-25 21:53:28', '2018-07-25 21:53:28');
INSERT INTO `role_permission` VALUES ('34', '5', '8', '2018-07-25 21:53:28', '2018-07-25 21:53:28');
INSERT INTO `role_permission` VALUES ('35', '6', '8', '2018-07-25 21:53:28', '2018-07-25 21:53:28');
INSERT INTO `role_permission` VALUES ('36', '7', '2', '2018-07-25 21:53:47', '2018-07-25 21:53:47');
INSERT INTO `role_permission` VALUES ('37', '3', '2', '2018-07-25 21:53:47', '2018-07-25 21:53:47');
INSERT INTO `role_permission` VALUES ('38', '4', '2', '2018-07-25 21:53:47', '2018-07-25 21:53:47');
INSERT INTO `role_permission` VALUES ('39', '5', '2', '2018-07-25 21:53:47', '2018-07-25 21:53:47');
INSERT INTO `role_permission` VALUES ('40', '6', '2', '2018-07-25 21:53:47', '2018-07-25 21:53:47');
INSERT INTO `role_permission` VALUES ('41', '7', '7', '2018-07-25 21:53:49', '2018-07-25 21:53:49');
INSERT INTO `role_permission` VALUES ('42', '3', '7', '2018-07-25 21:53:49', '2018-07-25 21:53:49');
INSERT INTO `role_permission` VALUES ('43', '4', '7', '2018-07-25 21:53:49', '2018-07-25 21:53:49');
INSERT INTO `role_permission` VALUES ('44', '5', '7', '2018-07-25 21:53:49', '2018-07-25 21:53:49');
INSERT INTO `role_permission` VALUES ('45', '6', '7', '2018-07-25 21:53:49', '2018-07-25 21:53:49');
INSERT INTO `role_permission` VALUES ('51', '7', '1', '2018-08-19 19:40:54', '2018-08-19 19:40:54');
INSERT INTO `role_permission` VALUES ('52', '3', '1', '2018-08-19 19:40:54', '2018-08-19 19:40:54');
INSERT INTO `role_permission` VALUES ('53', '4', '1', '2018-08-19 19:40:54', '2018-08-19 19:40:54');
INSERT INTO `role_permission` VALUES ('54', '5', '1', '2018-08-19 19:40:54', '2018-08-19 19:40:54');
INSERT INTO `role_permission` VALUES ('55', '6', '1', '2018-08-19 19:40:54', '2018-08-19 19:40:54');

-- ----------------------------
-- Table structure for seckill
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` varchar(120) NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '库存数量',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '秒杀开启时间',
  `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '秒杀结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `goods_id` int(11) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `status` varchar(5) DEFAULT NULL,
  `create_user` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1010 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';


-- ----------------------------
-- Records of seckill
-- ----------------------------
INSERT INTO seckill.seckill (seckill_id, name, number, start_time, end_time, create_time, goods_id, price, status, create_user) VALUES (1000, 'iphone6', 1247, '2017-07-03 23:19:00', '2027-06-25 00:00:00', '2016-06-24 19:46:06', 1, 8000000.00, null, null);
INSERT INTO seckill.seckill (seckill_id, name, number, start_time, end_time, create_time, goods_id, price, status, create_user) VALUES (1001, 'ipad2', 0, '2018-08-19 22:25:35', '2027-06-25 12:00:00', '2016-06-24 19:46:06', 2, 3000.00, '2', null);
INSERT INTO seckill.seckill (seckill_id, name, number, start_time, end_time, create_time, goods_id, price, status, create_user) VALUES (1002, '小米4', 100, '2018-08-19 22:30:22', '2027-06-25 12:00:00', '2016-06-24 19:46:06', 3, 1000.00, '1', null);
INSERT INTO seckill.seckill (seckill_id, name, number, start_time, end_time, create_time, goods_id, price, status, create_user) VALUES (1003, '红米note', 9, '2018-08-19 22:25:40', '2027-06-25 12:00:00', '2016-06-24 19:46:06', 4, 400.00, '1', null);
INSERT INTO seckill.seckill (seckill_id, name, number, start_time, end_time, create_time, goods_id, price, status, create_user) VALUES (1004, '小米6', 10, '2018-08-19 22:25:44', '2027-06-25 12:00:00', '2017-06-09 00:13:54', 5, 2000.00, '1', null);
INSERT INTO seckill.seckill (seckill_id, name, number, start_time, end_time, create_time, goods_id, price, status, create_user) VALUES (1005, '小米max2', 1000, '2018-08-19 22:25:45', '2027-06-25 12:00:00', '2017-06-10 11:14:49', 6, 1499.00, null, null);
INSERT INTO seckill.seckill (seckill_id, name, number, start_time, end_time, create_time, goods_id, price, status, create_user) VALUES (1006, '小米5c', 100, '2018-08-19 22:25:47', '2027-06-25 12:00:00', '2017-06-10 11:16:48', 7, 1299.00, null, null);

-- ----------------------------
-- Table structure for success_killed
-- ----------------------------
DROP TABLE IF EXISTS `success_killed`;
CREATE TABLE `success_killed` (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品',
  `user_phone` varchar(20) NOT NULL COMMENT '用户手机号',
  `status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '状态标示：-1：无效   0：成功   1：已付款',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- ----------------------------
-- Records of success_killed
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
create table user
(
    id              int unsigned auto_increment
        primary key,
    account         varchar(45)                           not null,
    password        varchar(45) default 'aa123456'        not null,
    create_time     timestamp   default CURRENT_TIMESTAMP not null,
    update_time     timestamp   default CURRENT_TIMESTAMP not null,
    username        varchar(50)                           null,
    locked          int         default 0                 null,
    avatar          varchar(500)                          null comment '用户头像url',
    last_login_time timestamp                             null,
    mobile          varchar(20)                           null,
    email_addr      varchar(100)                          null,
    constraint account_UNIQUE
        unique (account)
)
    charset = utf8mb3;

INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (14, 'test01', 'fad7b1af11bc7132037e94b8291a5ed3', '2016-12-18 17:53:26', '2017-06-17 14:46:21', '测试用户1', 0, null, '2022-06-25 17:19:39', '18888888888', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (15, 'test02', '7dae8fa826b9f038780a288831b52acd', '2018-07-14 21:25:50', '2018-07-14 21:25:50', '测试用户2', 0, null, '2022-06-25 17:19:37', '18888888889', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (21, 'admin123', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2018-07-21 21:43:47', '2018-07-21 21:43:47', '系统管理员', 0, null, '2022-06-25 15:13:11', '18888888890', 'techa@foxmail.com');
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (39, 'test04', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user04', 0, null, '2022-06-25 10:55:56', '18888888891', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (40, 'test05', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user05', 1, null, '2022-06-25 10:55:56', '18888888892', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (41, 'test06', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user06', 0, null, '2022-06-25 10:55:56', '18888888893', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (42, 'test07', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user07', 0, null, '2022-06-25 10:55:56', '18888888894', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (43, 'test08', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user08', 0, null, '2022-06-25 10:55:56', '18888888895', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (44, 'test09', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user09', 0, null, '2022-06-25 10:55:56', '18888888896', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (45, 'test10', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user10', 0, null, '2022-06-25 10:55:56', '18888888897', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (46, 'test11', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user11', 0, null, '2022-06-25 10:55:56', '18888888898', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (47, 'test12', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user12', 0, null, '2022-06-25 10:55:56', '18888888899', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (48, 'test13', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user13', 0, null, '2022-06-25 10:55:56', '18888888900', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (49, 'test14', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user14', 0, null, '2022-06-25 10:55:56', '18888888901', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (50, 'test15', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user15', 0, null, '2022-06-25 10:55:56', '18888888902', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (51, 'test16', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user16', 0, null, '2022-06-25 10:55:56', '18888888903', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (52, 'test17', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user17', 0, null, '2022-06-25 10:55:56', '18888888904', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (53, 'test18', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user18', 0, null, '2022-06-25 10:55:56', '18888888905', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (54, 'test19', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user19', 0, null, '2022-06-25 10:55:56', '18888888906', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (55, 'test20', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user20', 0, null, '2022-06-25 10:55:56', '18888888907', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (56, 'test21', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user21', 0, null, '2022-06-25 10:55:56', '18888888908', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (57, 'test22', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user22', 0, null, '2022-06-25 10:55:56', '18888888909', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (58, 'test23', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user23', 0, null, '2022-06-25 10:55:56', '18888888910', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (59, 'test24', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user24', 0, null, '2022-06-25 10:55:56', '18888888911', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (60, 'test25', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user25', 0, null, '2022-06-25 10:55:56', '18888888912', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (61, 'test26', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user26', 0, null, '2022-06-25 10:55:56', '18888888913', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (62, 'test27', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user27', 0, null, '2022-06-25 10:55:56', '18888888914', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (63, 'test28', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user28', 0, null, '2022-06-25 10:55:56', '18888888915', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (64, 'test29', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user29', 0, null, '2022-06-25 10:55:56', '18888888916', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (65, 'test30', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user30', 0, null, '2022-06-25 10:55:56', '18888888917', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (66, 'test31', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user31', 0, null, '2022-06-25 10:55:56', '18888888918', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (67, 'test32', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user32', 0, null, '2022-06-25 10:55:56', '18888888919', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (68, 'test33', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user33', 0, null, '2022-06-25 10:55:56', '18888888920', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (69, 'test34', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user34', 0, null, '2022-06-25 10:55:56', '18888888921', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (70, 'test35', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user35', 0, null, '2022-06-25 10:55:56', '18888888922', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (71, 'test36', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user36', 0, null, '2022-06-25 10:55:56', '18888888923', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (72, 'test37', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user37', 0, null, '2022-06-25 10:55:56', '18888888924', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (73, 'test38', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user38', 0, null, '2022-06-25 10:55:56', '18888888925', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (74, 'test39', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user39', 0, null, '2022-06-25 10:55:56', '18888888926', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (75, 'test40', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user40', 0, null, '2022-06-25 10:55:56', '18888888927', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (76, 'test41', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user41', 0, null, '2022-06-25 10:55:56', '18888888928', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (77, 'test42', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user42', 0, null, '2022-06-25 10:55:56', '18888888929', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (78, 'test43', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user43', 0, null, '2022-06-25 10:55:56', '18888888930', null);
INSERT INTO seckill.user (id, account, password, create_time, update_time, username, locked, avatar, last_login_time, mobile, email_addr) VALUES (79, 'test44', 'b9cd5964cd1dda92e2335cbfbee3ecc0', '2022-06-25 11:00:13', '2022-06-25 11:00:13', 'user44', 0, null, '2022-06-25 10:55:56', '18888888931', null);


DROP TABLE IF EXISTS `user_auth_account`;
create table if not exists `user_auth_account`
(
    id bigint not null
    primary key,
    user_id int null comment '用户id',
    third_account_id varchar(200) null comment '第三方账号id',
    third_account_name varchar(100) null comment '关联的第三方账户名称',
    source_type varchar(50) null comment '第三方授权来源',
    create_time datetime null,
    update_time datetime null
    )
    comment '用户第三方授权账号表';

INSERT INTO user_auth_account (id, user_id, third_account_id, third_account_name, source_type, create_time, update_time) VALUES (1, 21, null, 'techa', 'gitee', '2021-09-24 22:29:51', '2021-09-24 22:29:51');
INSERT INTO user_auth_account (id, user_id, third_account_id, third_account_name, source_type, create_time, update_time) VALUES (2, 21, null, 'techa', 'goodskill', '2021-09-24 22:29:51', '2021-09-24 22:29:51');
INSERT INTO user_auth_account (id, user_id, third_account_id, third_account_name, source_type, create_time, update_time) VALUES (3, 21, null, 'zheng', 'github', '2021-09-24 22:29:51', '2021-09-24 22:29:51');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned DEFAULT NULL,
  `role_id` int(10) unsigned DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `for_user_role_id` (`role_id`),
  KEY `for_user_id` (`user_id`),
  CONSTRAINT `for_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `for_user_role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES ('60', '15', '1', '2018-07-24 14:43:18', '2018-07-24 14:43:18');
INSERT INTO `user_role` VALUES ('61', '15', '2', '2018-07-24 14:43:18', '2018-07-24 14:43:18');
INSERT INTO `user_role` VALUES ('62', '15', '7', '2018-07-24 14:43:18', '2018-07-24 14:43:18');
INSERT INTO `user_role` VALUES ('63', '15', '8', '2018-07-24 14:43:18', '2018-07-24 14:43:18');
INSERT INTO `user_role` VALUES ('64', '21', '1', '2018-07-24 14:43:21', '2018-07-24 14:43:21');
INSERT INTO `user_role` VALUES ('65', '21', '2', '2018-07-24 14:43:21', '2018-07-24 14:43:21');
INSERT INTO `user_role` VALUES ('66', '21', '7', '2018-07-24 14:43:21', '2018-07-24 14:43:21');
INSERT INTO `user_role` VALUES ('67', '21', '8', '2018-07-24 14:43:21', '2018-07-24 14:43:21');
INSERT INTO `user_role` VALUES ('80', '14', '1', '2018-08-19 19:40:36', '2018-08-19 19:40:36');
INSERT INTO `user_role` VALUES ('81', '14', '2', '2018-08-19 19:40:36', '2018-08-19 19:40:36');
INSERT INTO `user_role` VALUES ('82', '14', '7', '2018-08-19 19:40:36', '2018-08-19 19:40:36');
INSERT INTO `user_role` VALUES ('83', '14', '8', '2018-08-19 19:40:36', '2018-08-19 19:40:36');

ALTER TABLE `seckill`.`success_killed`
ADD COLUMN `server_ip` VARCHAR(200) NULL AFTER `create_time`,
ADD COLUMN `user_ip` VARCHAR(200) NULL AFTER `server_ip`,
ADD COLUMN `user_id` VARCHAR(45) NULL AFTER `user_ip`;

CREATE TABLE `undo_log` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `branch_id` bigint(20) NOT NULL,
                            `xid` varchar(100) NOT NULL,
                            `context` varchar(128) NOT NULL,
                            `rollback_info` longblob NOT NULL,
                            `log_status` int(11) NOT NULL,
                            `log_created` datetime NOT NULL,
                            `log_modified` datetime NOT NULL,
                            `ext` varchar(100) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

create schema seckill_01 collate utf8mb4_0900_ai_ci;

create table success_killed_0
(
	seckill_id bigint not null comment '秒杀商品',
	user_phone varchar(20) not null comment '用户手机号',
	status tinyint default -1 not null comment '状态标示：-1：无效   0：成功   1：已付款',
	create_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '创建时间',
	server_ip varchar(200) null,
	user_ip varchar(200) null,
	user_id varchar(45) null,
	primary key (seckill_id, user_phone)
)
comment '秒杀成功明细表' charset=utf8;

create index idx_create_time
	on success_killed_0 (create_time);

create table success_killed_1
(
	seckill_id bigint not null comment '秒杀商品',
	user_phone varchar(20) not null comment '用户手机号',
	status tinyint default -1 not null comment '状态标示：-1：无效   0：成功   1：已付款',
	create_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '创建时间',
	server_ip varchar(200) null,
	user_ip varchar(200) null,
	user_id varchar(45) null,
	primary key (seckill_id, user_phone)
)
comment '秒杀成功明细表' charset=utf8;

create index idx_create_time
	on success_killed_1 (create_time);

create table seckill_01.success_killed_0
(
	seckill_id bigint not null comment '秒杀商品',
	user_phone varchar(20) not null comment '用户手机号',
	status tinyint default -1 not null comment '状态标示：-1：无效   0：成功   1：已付款',
	create_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '创建时间',
	server_ip varchar(200) null,
	user_ip varchar(200) null,
	user_id varchar(45) null,
	primary key (seckill_id, user_phone)
)
comment '秒杀成功明细表' charset=utf8;

create index idx_create_time
	on seckill_01.success_killed_0 (create_time);

create table seckill_01.success_killed_1
(
	seckill_id bigint not null comment '秒杀商品',
	user_phone varchar(20) not null comment '用户手机号',
	status tinyint default -1 not null comment '状态标示：-1：无效   0：成功   1：已付款',
	create_time timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '创建时间',
	server_ip varchar(200) null,
	user_ip varchar(200) null,
	user_id varchar(45) null,
	primary key (seckill_id, user_phone)
)
comment '秒杀成功明细表' charset=utf8;

create index idx_create_time
	on seckill_01.success_killed_1 (create_time);

create schema gs_sys collate utf8mb4_0900_ai_ci;
create table gs_sys.sys_dict_biz
(
    id          char(32)                not null comment '主键'
        primary key,
    parent_id   char(32)     default '' null comment '父主键',
    dict_code   varchar(255)            null comment '字典码',
    dict_key    varchar(255)            null comment '字典值',
    dict_value  varchar(255)            null comment '字典名称',
    description varchar(255) default '' null comment '字典备注',
    sort_no     int          default 0  null comment '排序',
    status      tinyint(1)   default 0  null comment '是否禁用：0-否；1-是；',
    del_flag    tinyint(1)   default 0  null comment '是否已删除：0-否；1-是；',
    root_flag   tinyint(1)   default 0  null comment '是否为父节点',
    sys_code    varchar(10)             null comment '系统编码',
    update_user varchar(32)             null comment '更新人',
    update_time datetime                null comment '更新时间',
    create_user varchar(32)             null comment '创建人',
    create_time datetime                null comment '创建时间'
)
    comment '业务字典表';

create table gs_sys.sys_dict
(
    id          char(32)                not null comment '主键'
        primary key,
    parent_id   char(32)     default '' null comment '父主键',
    dict_code   varchar(255)            null comment '字典码',
    dict_key    varchar(255)            null comment '字典值',
    dict_value  varchar(255)            null comment '字典名称',
    description varchar(255) default '' null comment '字典备注',
    sort_no     int          default 0  null comment '排序',
    status      tinyint(1)   default 0  null comment '是否禁用：0-否；1-是；',
    del_flag    tinyint(1)   default 0  null comment '是否已删除：0-否；1-是；',
    root_flag   tinyint(1)   default 0  null comment '是否为父节点',
    update_user varchar(32)             null comment '更新人',
    update_time datetime                null comment '更新时间',
    create_user varchar(32)             null comment '创建人',
    create_time datetime                null comment '创建时间'
)
    comment '字典表';

CREATE TABLE gs_sys.`undo_log` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `branch_id` bigint(20) NOT NULL,
                            `xid` varchar(100) NOT NULL,
                            `context` varchar(128) NOT NULL,
                            `rollback_info` longblob NOT NULL,
                            `log_status` int(11) NOT NULL,
                            `log_created` datetime NOT NULL,
                            `log_modified` datetime NOT NULL,
                            `ext` varchar(100) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

alter table seckill.`user`
    add avatar varchar(500) null comment '用户头像url';


