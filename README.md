# Open-Aerion-Integrated-Management-Platform (Open-AIMP)

Open-AIMP 是一个专为飞行模拟环境设计的综合飞控中心。它提供了模拟飞行操作、用户管理和系统监控的集成管理能力。
本项目经过架构重构，采用高效的**单体架构**设计，旨在提供轻量级、易部署且高性能的解决方案，无需复杂的微服务基础设施。

## 🚀 功能特性

*   **飞控中心**：管理模拟飞行操作的核心功能模块。
*   **单体架构**：基于 Spring Boot 单体设计，简化部署运维，降低资源消耗，适合中小型团队快速迭代。
*   **FSD 服务**：内置 Go 语言编写的 FSD (Flight Simulator Daemon) 协议服务，支持主流飞行模拟器连接，提供实时飞行数据交互。
*   **用户管理**：集成 Sa-Token，提供基于 JWT 和 Redis 的安全认证、权限管理和会话控制。
*   **现代化前端**：基于 Vue 3 + Vite 构建的响应式管理界面，提供流畅的用户体验。

## 🛠 技术栈

### 后端 (Backend)
*   **开发语言**：Java 17
*   **核心框架**：Spring Boot 3.2.4
*   **安全认证**：Sa-Token (JWT, Redis)
*   **数据库**：MySQL 8.0 (主数据), Redis (缓存与会话)
*   **ORM 框架**：Spring Data JPA
*   **工具库**：Lombok

### 前端 (Frontend)
*   **框架**：Vue.js 3
*   **构建工具**：Vite
*   **样式库**：Tailwind CSS
*   **语言**：JavaScript / TypeScript

### FSD 服务 (Flight Simulator Daemon)
*   **开发语言**：Go (Golang)
*   **架构**：高性能并发网络服务
*   **数据库支持**：PostgreSQL / SQLite

## 📂 项目结构

```
open-aimp/
├── backend/                # Java 后端单体应用 (端口: 8080)
│   ├── src/                # 源代码 (Controller, Service, Repository, Entity)
│   └── pom.xml             # Maven 依赖配置
├── frontend/               # Vue.js 前端应用 (端口: 5173)
│   ├── src/                # 前端源代码 (Views, Components, Stores)
│   └── vite.config.js      # Vite 配置
├── fsd/                    # Go FSD 服务 (飞行模拟协议处理)
│   ├── db/                 # 数据库迁移与模型
│   └── main.go             # 服务入口
└── README.md               # 项目说明文档
```

## 🏁 快速开始

### 环境要求

请确保您的开发环境已安装以下软件：
*   **Java JDK**: 版本 17 或更高
*   **Maven**: 版本 3.6 或更高
*   **Node.js**: 版本 20.19.0+
*   **Go**: 版本 1.20+ (如果需要运行 FSD)
*   **MySQL**: 版本 8.0+
*   **Redis**: 最新稳定版

### 部署步骤

#### 1. 基础设施准备
1.  启动 **MySQL** 服务器，并创建一个名为 `aimp` 的数据库。
2.  启动 **Redis** 服务器（默认端口 6379）。

#### 2. 后端启动 (Backend)
1.  进入后端目录：
    ```bash
    cd backend
    ```
2.  检查配置文件 `src/main/resources/application.yml`，确保数据库连接信息（URL, Username, Password）与您的环境一致。
3.  构建并启动项目：
    ```bash
    mvn clean spring-boot:run
    ```
    服务将在 `http://localhost:8080` 启动。

#### 3. 前端启动 (Frontend)
1.  进入前端目录：
    ```bash
    cd frontend
    ```
2.  安装依赖：
    ```bash
    npm install
    ```
3.  启动开发服务器：
    ```bash
    npm run dev
    ```
4.  访问应用：`http://localhost:5173`

#### 4. FSD 服务启动 (可选)
如果您需要支持飞行模拟器连接：
1.  进入 fsd 目录：
    ```bash
    cd fsd
    ```
2.  安装依赖并运行：
    ```bash
    go mod tidy
    go run main.go
    ```

## 🤝 参与贡献

欢迎提交 Pull Request 或 Issue 来参与项目贡献！我们欢迎任何形式的改进建议。
