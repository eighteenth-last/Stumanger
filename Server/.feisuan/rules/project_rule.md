
# 开发规范指南

为保证代码质量、可维护性、安全性与可扩展性，请在开发过程中严格遵循以下规范。

## 一、项目环境信息

- **操作系统**：Windows 11  
- **工作目录路径**：`D:\Code_decom\Stumanger\Server`  
- **开发者**：QQ327  
- **构建工具**：Maven  
- **语言版本**：Java 17  
- **框架版本**：
  - Spring Boot 3.1.5（基于 `spring-boot-starter-parent`）
  - MyBatis Plus 3.5.4.1  
  - JWT 0.11.5  

---

## 二、目录结构说明

```
Server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/student/
│   │   │       ├── common/
│   │   │       │   ├── exception/
│   │   │       │   ├── result/
│   │   │       │   └── utils/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       │   └── enums/
│   │   │       ├── exception/
│   │   │       ├── mapper/
│   │   │       ├── security/
│   │   │       └── service/
│   │   │           └── impl/
│   │   └── resources/
│   │       ├── mapper/
│   │       └── sql/
│   └── test/
│       ├── java/
│       │   └── com/student/
│       │       ├── controller/
│       │       ├── entity/
│       │       ├── mapper/
│       │       └── service/
│       └── resources/
└── pom.xml
```

> 📌 所有 Java 源码应放置于 `src/main/java/com/student/` 下，测试代码放在 `src/test/java/com/student/` 中。资源文件存放于 `src/main/resources` 和 `src/test/resources` 目录下。

---

## 三、技术栈要求

### 核心依赖：

| 组件名称              | 版本 / 描述                             |
|----------------------|----------------------------------------|
| Spring Boot Starter Web | `spring-boot-starter-web`            |
| Spring Security      | `spring-boot-starter-security`         |
| Validation           | `spring-boot-starter-validation`       |
| MySQL Connector      | `mysql-connector-j`                    |
| MyBatis Plus         | `mybatis-plus-boot-starter` (v3.5.4.1) |
| JWT                  | `jjwt-api/jjwt-impl/jjwt-jackson` (v0.11.5) |
| Apache POI           | `poi-ooxml` (v5.2.4)                   |
| Commons Lang3        | `commons-lang3`                        |

### 构建工具：
- 使用 Maven 进行项目构建和管理依赖。
- 使用 `spring-boot-maven-plugin` 实现打包运行。

---

## 四、分层架构规范

| 层级        | 职责说明                         | 开发约束与注意事项                                               |
|-------------|----------------------------------|----------------------------------------------------------------|
| **Controller** | 处理 HTTP 请求与响应，定义 API 接口 | 不得直接访问数据库，必须通过 Service 层调用                  |
| **Service**    | 实现业务逻辑、事务管理与数据校验   | 必须通过 Mapper 层访问数据库；返回 DTO 而非 Entity（除非必要） |
| **Mapper**     | 数据库访问与持久化操作             | 使用 MyBatis Plus 提供的接口进行 CRUD 操作                     |
| **Entity**     | 映射数据库表结构                   | 包名统一为 `entity`；不直接暴露给前端                       |
| **DTO**        | 数据传输对象                       | 用于前后端交互的数据封装                                     |
| **Common**     | 公共组件模块                       | 包括异常处理、结果封装、工具类等                             |

### 接口与实现分离

- 所有接口实现类需放在接口所在包下的 `impl` 子包中。

---

## 五、安全与性能规范

### 输入校验

- 使用 `@Valid` 与 JSR-303 校验注解（如 `@NotBlank`, `@Size` 等）
  - 注意：Spring Boot 3.x 中校验注解位于 `jakarta.validation.constraints.*`

- 禁止手动拼接 SQL 字符串，防止 SQL 注入攻击。

### 事务管理

- `@Transactional` 注解仅用于 **Service 层**方法。
- 避免在循环中频繁提交事务，影响性能。

### 安全策略

- 使用 Spring Security 实现认证授权机制；
- JWT 用于 Token 认证，密钥配置在 `application.yml` 中；
- 敏感信息不应明文存储或打印日志。

---

## 六、代码风格规范

### 命名规范

| 类型       | 命名方式             | 示例                  |
|------------|----------------------|-----------------------|
| 类名       | UpperCamelCase       | `UserServiceImpl`     |
| 方法/变量  | lowerCamelCase       | `saveUser()`          |
| 常量       | UPPER_SNAKE_CASE     | `MAX_LOGIN_ATTEMPTS`  |

### 注释规范

- 所有类、方法、字段需添加 **Javadoc** 注释。
- 注释采用中文编写（用户第一语言）

### 类型命名规范（阿里巴巴风格）

| 后缀 | 用途说明                     | 示例         |
|------|------------------------------|--------------|
| DTO  | 数据传输对象                 | `UserDTO`    |
| DO   | 数据库实体对象               | `UserDO`     |
| BO   | 业务逻辑封装对象             | `UserBO`     |
| VO   | 视图展示对象                 | `UserVO`     |
| Query| 查询参数封装对象             | `UserQuery`  |

### 实体类简化工具

- 使用 Lombok 注解替代手动编写 getter/setter/构造方法：
  - `@Data`
  - `@NoArgsConstructor`
  - `@AllArgsConstructor`

---

## 七、扩展性与日志规范

### 接口优先原则

- 所有业务逻辑通过接口定义（如 `UserService`），具体实现放在 `impl` 包中（如 `UserServiceImpl`）。

### 日志记录

- 使用 `@Slf4j` 注解代替 `System.out.println` 或其他简单输出方式；
- 日志级别建议使用 DEBUG / INFO / WARN / ERROR 分别对应不同严重程度的信息。

---

## 八、编码原则总结

| 原则       | 说明                                       |
|------------|--------------------------------------------|
| **SOLID**  | 高内聚、低耦合，增强可维护性与可扩展性     |
| **DRY**    | 避免重复代码，提高复用性                   |
| **KISS**   | 保持代码简洁易懂                           |
| **YAGNI**  | 不实现当前不需要的功能                     |
| **OWASP**  | 防范常见安全漏洞，如 SQL 注入、XSS 等      |

---
