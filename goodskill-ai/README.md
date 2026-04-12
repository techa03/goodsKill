# goodskill-ai 模块说明

基于 Spring AI Alibaba 构建的简易秒杀机器人服务，可以通过机器人服务完成服务调用，目前支持开启秒杀活动、输出活动耗时情况，以及知识库问答功能。

## 环境配置

### 1. 配置 API Key

进入 `goodskill-ai` 模块，替换配置 `spring.ai.dashscope.api-key`，可通过阿里云百炼平台获取。

### 2. 向量库配置（使用 PostgreSQL pgvector）

在 `application.yml` 文件中，配置 PostgreSQL 数据源：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/goodskill
    username: goodskill
    password: Password123
```

向量库会自动创建 pgvector 扩展和表结构。

### 3. 环境变量配置

启动 AI 服务前需要配置以下环境变量：

| 环境变量名 | 说明 | 示例 |
|---|---|---|
| `DASHSCOPE_API_KEY` | 阿里云百炼平台 API Key | `sk-xxxxxxxxxxxxxxxx` |
| `OLLAMA_HOST` | Ollama 服务地址（如使用 Ollama 模型） | `localhost` |
| `JINA_API_KEY` | Jina AI API Key（可选，用于 MCP 工具） | `jina_xxxxxxxxxxxxxxxx` |

**配置方式示例：**

```bash
# Linux/Mac
export DASHSCOPE_API_KEY=sk-xxxxxxxxxxxxxxxx
export OLLAMA_HOST=localhost
export JINA_API_KEY=jina_xxxxxxxxxxxxxxxx

# Windows PowerShell
$env:DASHSCOPE_API_KEY="sk-xxxxxxxxxxxxxxxx"
$env:OLLAMA_HOST="localhost"
$env:JINA_API_KEY="jina_xxxxxxxxxxxxxxxx"
```

## 启动服务

main 方法运行 `AiBotApplication` 类，访问聊天页面 `http://127.0.0.1:18077`

## API 接口说明

### 基础对话

* **聊天页面**：`http://127.0.0.1:18077`
* **RAG 知识库管理页面**：`http://127.0.0.1:18077/index_rag.html`

### 知识库管理 API

AI 机器人支持知识库问答功能，可以通过上传文档或录入文本知识来增强机器人的回答能力：

| 接口 | 方法 | 说明 |
|---|---|---|
| `/api/knowledge/files` | POST | 上传知识文档（文本文件） |
| `/api/knowledge/text` | POST | 录入文本内容 |
| `/api/knowledge` | GET | 查看已录入的知识文档列表 |
| `/api/knowledge/{id}` | DELETE | 删除指定知识文档 |

## 使用示例

以下是一个对话示例：

```text
我: 帮我执行秒杀

AI: 当然可以帮助您执行秒杀活动啦！为了顺利进行，请告诉我以下几个信息：
1. 秒杀活动的ID是多少？
2. 这次秒杀的商品数量是多少？
3. 您打算这次请求的次数是多少？
有了这些信息，我就能帮您开始了！😊

我: 秒杀活动id 1001，请求次数20，商品数量10个

AI: 秒杀活动已成功开启！我们现在等待10秒钟，然后我会为您获取任务的耗时统计信息。
秒杀活动已成功启动，并在10秒后获得了任务耗时统计信息：
总耗时：6.673773375秒
具体耗时分布如下：
- 秒杀活动id:1001，秒杀场景五(数据库原子性更新update set num = num -1)耗时 3.528280125 秒，占比 53%
- 秒杀活动id:1001，秒杀场景五(数据库原子性更新update set num = num -1)耗时 3.14549325 秒，占比 47%
希望这些信息对您有帮助！如果还有其他问题或需要进一步的帮助，请随时告诉我哦！😊
```

## 依赖服务

* PostgreSQL（带 pgvector 扩展）
* Nacos 服务注册中心
* 其他秒杀服务（goodskill-seckill-provider、goodskill-order-provider 等）
