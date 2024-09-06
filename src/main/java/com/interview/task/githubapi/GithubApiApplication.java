package com.interview.task.githubapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GithubApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubApiApplication.class, args);
    }
}
