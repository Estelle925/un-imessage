package com.unimessage.mcp;

import com.unimessage.mcp.config.McpProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author hm
 */
@SpringBootApplication
@EnableConfigurationProperties(McpProperties.class)
public class UniMessageMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniMessageMcpApplication.class, args);
    }
}
