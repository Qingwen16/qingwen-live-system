

<div align="center">

**青文直播平台后端系统**

基于 Spring Boot 3 + MyBatis-Plus 构建的私域直播平台后端服务

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.9-orange.svg)](https://baomidou.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)

</div>

---

## 📖 项目简介

青文直播是一个面向私域流量的直播平台系统，支持用户通过手机号登录，提供直播间管理、主播粉丝体系等核心功能。

### 核心特性

- 🔐 **用户体系** - 支持手机号一键登录
- 🎥 **直播管理** - 直播间创建、推流拉流、直播录制回放
- 💬 **实时互动** - WebSocket 实时弹幕、聊天消息
- 👥 **社交关系** - 关注订阅
- 🛡️ **平台管理** - 多级管理员、房间禁言、内容审核

---

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 3.4.1 | 核心框架 |
| MyBatis-Plus | 3.5.9 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | - | 缓存中间件 |
| WebSocket | - | 实时通信 |

### 依赖组件

- **Lombok** - 简化代码
- **Hutool** - Java 工具库
- **FastJSON** - JSON 序列化
- **MyBatis-Plus JSqlParser** - SQL 解析增强

---

## 📊 数据库设计

系统包含 16 个核心数据表，涵盖用户、直播、互动、资产四大模块：

### 核心实体

- **UserInfo** - 用户基本信息
- **AnchorInfo** - 主播信息
- **AdminInfo** - 管理员信息
- **LiveRoom** - 直播间信息
- **LiveCategory** - 直播分区

### 互动模块

- **chatMessage** (ChatMessage) - 弹幕消息
- **UserFollow** - 关注关系

### 管理模块

- **RoomMute** - 房间禁言
- **SystemMessage** - 系统消息
- **WebSocketSession** - 连接会话
- **LiveRecord** - 直播记录

---

