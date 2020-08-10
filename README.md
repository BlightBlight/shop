# shop

## 前言
该项目是一个电商系统，采用SpringBoot+MyBatis++Redis+RabbitMQ+ZooKeeper实现

## 项目介绍
该项目为个人作品，模仿淘宝、京东等在线电商系统，开发一个简单版电商系统。

### 技术选型
| 技术            | 说明                                   |
| --------------- | -------------------------------------- |
| SpringBoot      | 用于容器+MVC框架                       |
| MyBatis         | 用于封装MySQL数据库，使用SQL语句       |
| Redis           | 用于实现缓存                           |
| RabbitMQ        | 用于发送消息队列，对订单等模块进行削峰 |
| ZooKeeper       | 用于生成分布锁                         |
| ElasticSearch   | 用作搜索模块和日志收集（未实现）       |
| Spring-Security | 用于用户登录与权限管理                 |
| JWT             | 生成Token进行登录操作                  |
| logback         | 用于日志生成                           |

### 系统架构
| 模块           | 说明                 |
| -------------- | -------------------- |
| shop-common    | 通用模块             |
| shop-coupon    | 优惠券模块（TODO）   |
| shop-customer  | 用户模块             |
| shop-flashsale | 秒杀模块             |
| shop-goods     | 商品模块             |
| shop-order     | 订单模块             |
| shop-portal    | 页面展示模块（TODO） |
| shop-promotion | 活动模块（TODO）     |
| shop-search    | 搜索模块             |
| shop-security  | 权限模块             |
| shop-shopcart  | 购物车模块           |

### 模块介绍
- 通用模块用于放置配置文件、全局异常处理、通用返回对象、通用工具类等。
- 优惠券模块：暂未实现，思路如下：
所有优惠券作用、范围、限制等全部数据库化，代码不做处理。
按照游戏Buff思路实现优惠券作用，优惠券可对订单造成正面作用、负面作用，正面作用为折扣、直减、满减等，负面作用为不可叠加、与其余作用冲突等。
冲突作用使用列表将作用IdJSON化存储到数据库中，并赋予作用优先级，根据优先级判断优惠券是否可使用。
根据作用对订单计算公式产生影响，进而影响最终价格。
- 用户模块：基于SpringSecurity与JWT进行实现，使用Redis对token进行存储，放置到SpringSecurity对应的session空间进行管理。（不满意，应将JWT替换为Spring-Session配合更好）
- 秒杀模块：使用RabbitMQ进行消息削峰，使用Redis进行商品缓存，使用ZooKeeper保证订单一致性、库存一致性。
- 商品模块：使用Redis进行商品缓存，缓解MySQL压力，使用ZooKeeper保证商品数据一致性。
- 订单模块：使用Redis进行订单缓存，使用RabbitMQ进行订单延时删除。
- 页面展示模块：页面跳转，页面初始化等。
- 活动模块：秒杀模块前置模块，用于生成活动对秒杀模块进行管理。
- 搜索模块：使用Elasticsearch实现商品模块搜索功能，基于内存实现，速度快，功能强（未完全完善，不同语言未设置索引，文档未全部设置）
- 权限模块：使用SpringSecurity进行权限管理，用于登录、验证用户权限等。（未实现动态基于路径判断、权限决策等）
- 购物车模块：基本实现购物车功能，目前使用Redis进行数据管理（优化为静态页面，不使用Redis，数据存取都放到静态页面中）

### 项目搭建
- 配置相关Redis、ZooKeeper、RabbitMQ、MyBatis等账号密码Ip地址，启动shop-portal模块即可。shop-search模块单独启动。
