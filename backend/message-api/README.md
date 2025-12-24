# UniMessage SDK 集成文档

`message-api` 是 UniMessage 统一消息中心的 Java SDK，提供了基于 Spring Boot 的自动装配功能，能够快速集成到 Spring Boot
项目中，实现消息推送功能。

## 1. 引入依赖

在你的 Spring Boot 项目的 `pom.xml` 中添加如下依赖：

```xml
<dependency>
    <groupId>com.unimessage</groupId>
    <artifactId>message-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## 2. 配置参数

在 `application.yml` 或 `application.properties` 中配置 UniMessage 服务端地址和应用鉴权信息：

```yaml
un-imessage:
  client:
    # 服务端地址
    host: localhost
    # 服务端端口
    port: 8079
    # 是否使用 HTTPS
    use-ssl: false
    # 应用 Key (需在 UniMessage 管理后台获取)
    app-key: your-app-key
    # 应用 Secret (需在 UniMessage 管理后台获取)
    app-secret: your-app-secret
    # 连接超时时间 (毫秒，默认 5000)
    connect-timeout: 5000
    # 读取超时时间 (毫秒，默认 10000)
    read-timeout: 10000
```

## 3. 使用 SDK 发送消息

在你的业务代码中注入 `UniMessageClient` 接口即可调用发送消息的方法。

### 示例代码

```java
import com.unimessage.dto.SendRequest;
import com.unimessage.dto.SendResponse;
import com.unimessage.sdk.client.UniMessageClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Resource
    private UniMessageClient uniMessageClient;

    public void sendWelcomeMessage(String phoneNumber, String username) {
        // 构建发送请求
        SendRequest request = new SendRequest();

        // 1. 设置模板代码 (在 UniMessage 后台创建的模板 Code)
        request.setTemplateCode("WELCOME_SMS");

        // 2. 设置接收者 (支持多个)
        request.setRecipients(Collections.singletonList(phoneNumber));

        // 3. 设置模板参数 (根据模板内容定义)
        Map<String, Object> params = new HashMap<>();
        params.put("name", username);
        params.put("code", "123456");
        request.setParams(params);

        // 4. 设置业务ID (可选，用于追踪或幂等)
        request.setBizId("ORDER_" + System.currentTimeMillis());

        try {
            // 调用发送接口
            SendResponse response = uniMessageClient.send(request);

            if (response.isSuccess()) {
                System.out.println("消息发送成功，批次号: " + response.getBatchNo());
            } else {
                System.err.println("消息发送失败: " + response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 4. 自动装配原理

本 SDK 遵循 Spring Boot 3 的自动装配规范。

- **自动配置类**: `com.unimessage.sdk.autoconfigure.UniMessageAutoConfiguration`
- **注册文件**: `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

只要引入依赖并配置了 `unimessage.client` 相关参数，`UniMessageClient` Bean 就会自动注入到 Spring 容器中。
