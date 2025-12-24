package com.unimessage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 海明
 */
@SpringBootApplication
@MapperScan("com.unimessage.mapper")
public class UniMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniMessageApplication.class, args);
    }

}
