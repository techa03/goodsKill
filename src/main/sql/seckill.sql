/*
Navicat MySQL Data Transfer

Source Server         : seckill
Source Server Version : 50712
Source Host           : localhost:3306
Source Database       : seckill

Target Server Type    : MYSQL
Target Server Version : 50712
File Encoding         : 65001

Date: 2017-01-07 16:32:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `goods_id` int(11) NOT NULL AUTO_INCREMENT,
  `photo_url` varchar(200) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `price` varchar(45) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `introduce` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('1', 'I:/java学习/Img411124043.jpg', 'iphone6', '4000', null, null);
INSERT INTO `goods` VALUES ('2', 'I:/java学习/o201311031719473392.jpg', 'ipad2', '1900', null, null);
INSERT INTO `goods` VALUES ('3', 'I:/java学习/mi4_2.png', '小米4', '800', null, null);
INSERT INTO `goods` VALUES ('4', 'I:/java学习/FsR53T_SdyRHj9bx_E56Qz9PBhxV.jpg!580x580.jpg', '红米note', '699', null, null);

-- ----------------------------
-- Table structure for seckill
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
  `name` varchar(120) NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '库存数量',
  `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '秒杀开启时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `goods_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- ----------------------------
-- Records of seckill
-- ----------------------------
INSERT INTO `seckill` VALUES ('1000', '1000元秒杀iphone6', '100', '2017-01-07 11:01:47', '2016-07-22 00:00:00', '2016-06-24 19:46:06', '1');
INSERT INTO `seckill` VALUES ('1001', '500元秒杀ipad2', '100', '2017-01-07 11:01:47', '2015-11-02 00:00:00', '2016-06-24 19:46:06', '2');
INSERT INTO `seckill` VALUES ('1002', '300元秒杀小米4', '100', '2017-01-07 11:01:47', '2015-11-02 00:00:00', '2016-06-24 19:46:06', '3');
INSERT INTO `seckill` VALUES ('1003', '200元秒杀红米note', '100', '2017-01-07 11:01:47', '2015-11-02 00:00:00', '2016-06-24 19:46:06', '4');

-- ----------------------------
-- Table structure for success_killed
-- ----------------------------
DROP TABLE IF EXISTS `success_killed`;
CREATE TABLE `success_killed` (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品',
  `user_phone` bigint(20) NOT NULL COMMENT '用户手机号',
  `status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '状态标示：-1：无效   0：成功   1：已付款',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- ----------------------------
-- Records of success_killed
-- ----------------------------
INSERT INTO `success_killed` VALUES ('1', '1373483423', '0', '2016-08-06 16:36:39');
INSERT INTO `success_killed` VALUES ('1000', '1373483423', '0', '2016-07-06 18:20:25');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL DEFAULT 'aa123456',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_UNIQUE` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'techa', '47342d1bee153385294760bddb8a7f49', '2016-12-18 17:53:26', null);
INSERT INTO `user` VALUES ('5', 'techa03', 'daa857d46518b530c9800e491a928d3f', '2016-12-22 20:01:34', null);
INSERT INTO `user` VALUES ('6', 'techa04', 'daa857d46518b530c9800e491a928d3f', '2016-12-22 20:05:06', null);
INSERT INTO `user` VALUES ('10', 'techa06', 'daa857d46518b530c9800e491a928d3f', '2016-12-22 21:06:14', null);
INSERT INTO `user` VALUES ('11', 'techa05', 'daa857d46518b530c9800e491a928d3f', '2016-12-22 21:13:13', null);
INSERT INTO `user` VALUES ('12', 'techa07', 'daa857d46518b530c9800e491a928d3f', '2016-12-22 21:36:29', null);
INSERT INTO `user` VALUES ('13', 'techa11', 'daa857d46518b530c9800e491a928d3f', '2016-12-22 21:38:18', null);
