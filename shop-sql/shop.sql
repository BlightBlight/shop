/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50536
 Source Host           : localhost:3306
 Source Schema         : shop

 Target Server Type    : MySQL
 Target Server Version : 50536
 File Encoding         : 65001

 Date: 10/08/2020 11:08:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for city
-- ----------------------------
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city`  (
  `id` int(11) NOT NULL DEFAULT 0,
  `pid` int(11) NULL DEFAULT NULL COMMENT '上级ID',
  `cityname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for conpon_effect
-- ----------------------------
DROP TABLE IF EXISTS `conpon_effect`;
CREATE TABLE `conpon_effect`  (
  `effect_id` bigint(20) NOT NULL COMMENT '作用Id',
  `effect_type_id` bigint(20) NOT NULL COMMENT '作用类型Id',
  `effect_number` decimal(10, 2) NOT NULL COMMENT '作用效果',
  `effect_priority` int(11) NOT NULL COMMENT '作用优先级',
  `repulsion_effect_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '排斥作用Id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`effect_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for coupon
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon`  (
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券Id',
  `coupon_description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '优惠券描述',
  `place_id` bigint(20) NOT NULL COMMENT '渠道Id',
  `place_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '渠道名称',
  `range_id` bigint(20) NOT NULL COMMENT '作用范围Id',
  `range_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作用范围名称',
  `effect_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作用Id(json存储)',
  `exclude_effect_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '排斥作用Id(json存储)',
  `total_number` int(11) NOT NULL COMMENT '总数量',
  `customer_limit_number` int(11) NOT NULL COMMENT '单用户限制数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `online_status` tinyint(1) NOT NULL COMMENT '0,未上线；1,已上线',
  `delete_status` tinyint(1) NOT NULL COMMENT '0,未删除；1,已删除',
  PRIMARY KEY (`coupon_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for coupon-act
-- ----------------------------
DROP TABLE IF EXISTS `coupon-act`;
CREATE TABLE `coupon-act`  (
  `coupon_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '优惠券Id',
  `act_sn` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '活动码',
  `coupon_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠券名称',
  `coupon_scope` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠券范围',
  `coupon_type` tinyint(1) NULL DEFAULT NULL COMMENT '0,固定优惠；1,浮动优惠',
  `getType` tinyint(1) NOT NULL COMMENT '0,手动领取；1,自动领取',
  `limit_type` tinyint(1) NULL DEFAULT NULL COMMENT '0,固定日期过期；1,领券后固定时间过期',
  `result` int(11) NULL DEFAULT NULL COMMENT '特殊设定',
  PRIMARY KEY (`coupon_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for coupon_place
-- ----------------------------
DROP TABLE IF EXISTS `coupon_place`;
CREATE TABLE `coupon_place`  (
  `place_id` bigint(20) NOT NULL COMMENT '渠道Id',
  `place_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '渠道名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`place_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for coupon_range
-- ----------------------------
DROP TABLE IF EXISTS `coupon_range`;
CREATE TABLE `coupon_range`  (
  `range_id` bigint(20) NOT NULL COMMENT '范围Id（Id须有意义，由相应范围类型的Id组成）',
  `range_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '范围名称（可为Category、Brand、Shop、邮费及其四种任意组合）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`range_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for coupon_type
-- ----------------------------
DROP TABLE IF EXISTS `coupon_type`;
CREATE TABLE `coupon_type`  (
  `type_id` bigint(20) NOT NULL COMMENT '作用类型Id',
  `type_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作用类型名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`type_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for customer_address
-- ----------------------------
DROP TABLE IF EXISTS `customer_address`;
CREATE TABLE `customer_address`  (
  `address_id` int(11) NOT NULL COMMENT '收货地址表',
  `customer_id` bigint(64) NOT NULL COMMENT '用户ID',
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '收件人姓名',
  `user_mobile` int(11) NOT NULL COMMENT '用户电话',
  `user_mobile2` int(11) NULL DEFAULT NULL COMMENT '备用电话',
  `country` int(11) NOT NULL COMMENT '国家',
  `provice` int(11) NOT NULL COMMENT '省份',
  `city` int(11) NOT NULL COMMENT '城市',
  `area` int(11) NOT NULL COMMENT '地区',
  `street` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '街道/详细收货地址',
  `default_status` tinyint(1) NOT NULL COMMENT '0,默认收货地址；1,非默认收货地址',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`address_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for customer_cartitem
-- ----------------------------
DROP TABLE IF EXISTS `customer_cartitem`;
CREATE TABLE `customer_cartitem`  (
  `cartitem_id` bigint(20) NOT NULL COMMENT '购物车选项ID',
  `customer_id` bigint(20) NOT NULL COMMENT '用户ID，直接用用户ID作为主键，主要是基于一个用户只能拥有唯一的一个购物车而考虑的',
  `shop_id` bigint(20) NOT NULL COMMENT '商铺ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'sku商品ID',
  `number` int(11) NOT NULL COMMENT '商品数量',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `spec_value_id` bigint(20) NOT NULL COMMENT '规格值ID',
  `spec_value_name` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '规格值名称',
  `goods_position` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品顺序保存位置',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `select_status` tinyint(1) NOT NULL COMMENT '1,未勾选；2,已勾选',
  `delete_status` tinyint(1) NOT NULL COMMENT '1,未删除；2,已删除',
  `buy_status` tinyint(1) NOT NULL COMMENT '1,未购买；2,已购买',
  PRIMARY KEY (`cartitem_id`) USING BTREE,
  INDEX `goods_position`(`goods_position`) USING BTREE COMMENT '商品顺序保存位置'
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for customer_coupon
-- ----------------------------
DROP TABLE IF EXISTS `customer_coupon`;
CREATE TABLE `customer_coupon`  (
  `customer_coupon_id` bigint(20) NOT NULL COMMENT '用户-优惠券表Id',
  `customer_id` bigint(20) NOT NULL COMMENT '用户Id',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券Id',
  `coupon_number` int(11) NULL DEFAULT NULL COMMENT '优惠券数量',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `coupon_status` tinyint(1) NOT NULL COMMENT '0,未使用；1,已使用',
  `delete_status` tinyint(1) NOT NULL COMMENT '0,未删除；1,已删除',
  PRIMARY KEY (`customer_coupon_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for customer_info
-- ----------------------------
DROP TABLE IF EXISTS `customer_info`;
CREATE TABLE `customer_info`  (
  `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_mobileNum` char(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户手机号',
  `customer_pwd` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `customer_salt` char(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '盐值',
  `customer_nickName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户昵称',
  `register_time` datetime NOT NULL COMMENT '注册时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `customer_status` tinyint(1) NOT NULL COMMENT '1,正常；2,冻结',
  `delete_status` tinyint(1) NOT NULL COMMENT '1,未删除；2,删除中；3,已删除',
  PRIMARY KEY (`customer_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for customer_login
-- ----------------------------
DROP TABLE IF EXISTS `customer_login`;
CREATE TABLE `customer_login`  (
  `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
  `customer_login_id` bigint(20) NOT NULL COMMENT '登录表ID',
  `customer_identity_type` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录类型\r\n0,账号；1，手机号；2,微信',
  `customer_identifiler` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户唯一标识',
  `customer_credential` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码凭证',
  `delete_status` tinyint(1) NOT NULL COMMENT '1,未删除；2,删除中；3,已删除',
  `login_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`customer_login_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for customer_order
-- ----------------------------
DROP TABLE IF EXISTS `customer_order`;
CREATE TABLE `customer_order`  (
  `order_item_id` bigint(20) NOT NULL COMMENT '主订单ID',
  `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
  `order_item_status` tinyint(4) NULL DEFAULT NULL COMMENT '0,下单(craete)；1,付款(pay)；2,卖家发货(deliver)；3,买家收货(receive)；4,退货(rereturn)',
  PRIMARY KEY (`order_item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for flashsale_goods
-- ----------------------------
DROP TABLE IF EXISTS `flashsale_goods`;
CREATE TABLE `flashsale_goods`  (
  `shop_id` bigint(20) NOT NULL COMMENT '商铺ID',
  `spu_id` bigint(20) NOT NULL COMMENT 'spu表ID',
  `flashsale_goods_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀表ID',
  `flashsale_goods_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '秒杀商品名称',
  `stock` int(11) NOT NULL COMMENT '秒杀库存',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '秒杀价',
  `spec_value_id` bigint(20) NOT NULL COMMENT '规格值表ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `verify_status` tinyint(1) NOT NULL COMMENT '0,未审核；1,已审核',
  `flashsale_goods_status` tinyint(1) NOT NULL COMMENT '0,未上架；1,已上架',
  `delete_status` tinyint(1) NOT NULL COMMENT '0,未删除；1,已删除',
  PRIMARY KEY (`flashsale_goods_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6689999600524197889 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for goods_brand
-- ----------------------------
DROP TABLE IF EXISTS `goods_brand`;
CREATE TABLE `goods_brand`  (
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `brand_id` bigint(20) NOT NULL COMMENT '品牌ID',
  `brand_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '品牌名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `delete_status` tinyint(1) NOT NULL COMMENT '1,未删除；2,已删除',
  PRIMARY KEY (`brand_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for goods_category
-- ----------------------------
DROP TABLE IF EXISTS `goods_category`;
CREATE TABLE `goods_category`  (
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `category_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分类名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `delete_status` tinyint(1) NOT NULL COMMENT '1,未删除；2,已删除',
  PRIMARY KEY (`category_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for goods_sku
-- ----------------------------
DROP TABLE IF EXISTS `goods_sku`;
CREATE TABLE `goods_sku`  (
  `shop_id` bigint(20) NULL DEFAULT NULL COMMENT '商铺ID',
  `spu_id` bigint(20) NOT NULL COMMENT 'spu表ID',
  `sku_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'sku表ID',
  `sku_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'sku商品名称（无则继承spu商品名称）',
  `stock` bigint(11) NOT NULL COMMENT '库存',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `spec_value_id` bigint(20) NOT NULL COMMENT '规格值表ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `verify_status` tinyint(1) NULL DEFAULT NULL COMMENT '0,未审核；1,审核中；2,已审核',
  `sku_status` tinyint(1) NULL DEFAULT NULL COMMENT '0,未上架；1,已上架；2,已下架',
  `delete_status` tinyint(1) NULL DEFAULT NULL COMMENT '0,未删除；1,已删除',
  PRIMARY KEY (`sku_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6690490214181240833 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for goods_spec
-- ----------------------------
DROP TABLE IF EXISTS `goods_spec`;
CREATE TABLE `goods_spec`  (
  `spec_id` bigint(20) NOT NULL COMMENT '规格表ID',
  `spec_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '规格名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `delete_status` tinyint(1) NULL DEFAULT NULL COMMENT '1,未删除；2,已删除',
  PRIMARY KEY (`spec_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for goods_spec_values
-- ----------------------------
DROP TABLE IF EXISTS `goods_spec_values`;
CREATE TABLE `goods_spec_values`  (
  `spec_id` bigint(20) NOT NULL COMMENT '规格表ID',
  `spec_value_id` bigint(20) NOT NULL COMMENT '规格值表ID',
  `spec_value_name` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '规格值',
  `create_time` date NOT NULL COMMENT '创建时间',
  `update_time` date NULL DEFAULT NULL COMMENT '修改时间',
  `delete_status` tinyint(1) NULL DEFAULT NULL COMMENT '0,未删除；1,已删除',
  PRIMARY KEY (`spec_value_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for goods_spu
-- ----------------------------
DROP TABLE IF EXISTS `goods_spu`;
CREATE TABLE `goods_spu`  (
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `brand_id` bigint(20) NULL DEFAULT NULL COMMENT '品牌ID',
  `pre_id` bigint(20) NULL DEFAULT NULL COMMENT '上级分类ID',
  `level` int(11) NULL DEFAULT NULL COMMENT '分类级别',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '型号',
  `spu_id` bigint(20) NOT NULL COMMENT 'spu表ID',
  `spu_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品名称',
  `spec_id` bigint(20) NULL DEFAULT NULL COMMENT '规格表ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `verify_status` tinyint(1) NULL DEFAULT NULL COMMENT '1,未审核；2,正在审核；3,已审核',
  `delete_status` tinyint(1) NULL DEFAULT NULL COMMENT '1,未删除；2,已删除',
  PRIMARY KEY (`spu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail`  (
  `order_detail_id` bigint(20) NOT NULL COMMENT '订单详情ID',
  `order_item_id` bigint(20) NOT NULL COMMENT '子订单ID',
  `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '商家ID',
  `sku_id` bigint(20) NOT NULL COMMENT '商品ID',
  `shop_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商家名称',
  `sku_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '商品数量',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `spec_value_id` bigint(20) NOT NULL COMMENT '规格值ID',
  `spec_value_name` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '规格值名称',
  `order_detail_price` decimal(10, 2) NOT NULL COMMENT '实付价格',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `comment_status` tinyint(1) NULL DEFAULT NULL COMMENT '1,未评论；2,已评论',
  PRIMARY KEY (`order_detail_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `order_item_id` bigint(20) NOT NULL COMMENT '子订单ID',
  `order_id` bigint(20) NOT NULL COMMENT '主订单ID',
  `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '商铺ID',
  `order_item_number` int(11) NOT NULL COMMENT '子订单商品总数量',
  `order_item_price` decimal(10, 2) NOT NULL COMMENT '子订单商品总金额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `order_item_status` tinyint(1) NOT NULL COMMENT '1,下单(create)；2,付款(pay)；3,卖家发货(deliver)；4,买家收货(receive)；5,退货(rereturn)',
  `delete_status` tinyint(4) NOT NULL COMMENT '1,未删除；2,伪删除；3,已删除',
  `cancel_status` tinyint(1) NOT NULL COMMENT '1,未取消；2,取消中；3,已取消',
  `refund_status` tinyint(1) NOT NULL COMMENT '1,无退款；2,退款中；3,部分退款；4,全退款',
  PRIMARY KEY (`order_item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for order_superior
-- ----------------------------
DROP TABLE IF EXISTS `order_superior`;
CREATE TABLE `order_superior`  (
  `order_id` bigint(20) NOT NULL COMMENT '主订单ID',
  `customer_id` bigint(20) NOT NULL COMMENT '用户ID',
  `order_number` int(11) NOT NULL COMMENT '总商品数量',
  `order_price` decimal(10, 2) NOT NULL COMMENT '总商品金额',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `delete_status` tinyint(1) NULL DEFAULT NULL COMMENT '1,未删除；2,伪删除；3,真的已删除',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for promotion
-- ----------------------------
DROP TABLE IF EXISTS `promotion`;
CREATE TABLE `promotion`  (
  `promotion_id` bigint(20) NOT NULL COMMENT '活动ID',
  `admin_id` bigint(20) NOT NULL COMMENT '管理员ID',
  `promotion_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '活动名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `promotion_status` tinyint(1) NOT NULL COMMENT '0,未上线；1,已上线',
  `delete_status` tinyint(1) NULL DEFAULT NULL COMMENT '0,未删除；1,已删除',
  PRIMARY KEY (`promotion_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for promotion_goods_relation
-- ----------------------------
DROP TABLE IF EXISTS `promotion_goods_relation`;
CREATE TABLE `promotion_goods_relation`  (
  `promotion_id` bigint(20) NOT NULL COMMENT '活动ID',
  `promotion_session_id` bigint(20) NOT NULL COMMENT '活动场次ID',
  `promotion_goods_relation_id` bigint(20) NOT NULL COMMENT '活动商品关系表ID',
  `goods_id` bigint(20) NOT NULL COMMENT '秒杀商品ID',
  `admin_id` bigint(20) NOT NULL COMMENT '管理员ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  PRIMARY KEY (`promotion_goods_relation_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for promotion_session
-- ----------------------------
DROP TABLE IF EXISTS `promotion_session`;
CREATE TABLE `promotion_session`  (
  `promotion_id` bigint(20) NOT NULL COMMENT '活动ID',
  `promotion_session_id` bigint(20) NOT NULL COMMENT '活动场次ID',
  `admin_id` bigint(20) NOT NULL COMMENT '管理员ID',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `promotion_session_status` tinyint(1) NOT NULL COMMENT '0,未启动；1,已启动',
  PRIMARY KEY (`promotion_session_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for seckill_order
-- ----------------------------
DROP TABLE IF EXISTS `seckill_order`;
CREATE TABLE `seckill_order`  (
  `seckill_order_id` bigint(255) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(11) NULL DEFAULT NULL,
  `order_id` bigint(255) NULL DEFAULT NULL,
  `goods_id` bigint(255) NULL DEFAULT NULL,
  PRIMARY KEY (`seckill_order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for shop
-- ----------------------------
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop`  (
  `shop_id` bigint(64) NOT NULL COMMENT '商铺ID',
  `shop_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商铺名称',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`shop_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for shop_order
-- ----------------------------
DROP TABLE IF EXISTS `shop_order`;
CREATE TABLE `shop_order`  (
  `order_item_id` bigint(20) NOT NULL COMMENT '子订单ID',
  `shop_id` bigint(20) NULL DEFAULT NULL COMMENT '商铺ID',
  `order_item_status` tinyint(4) NULL DEFAULT NULL COMMENT '0,下单(craete)；1,付款(pay)；2,卖家发货(deliver)；3,买家收货(receive)；4,退货(rereturn)',
  PRIMARY KEY (`order_item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
