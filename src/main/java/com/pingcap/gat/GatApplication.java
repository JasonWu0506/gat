package com.pingcap.gat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.pingcap.gat.mapper")
@EnableScheduling
public class GatApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatApplication.class, args);
    }

}
