package org.example.leavesystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LeavesystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeavesystemApplication.class, args);
    }

}
