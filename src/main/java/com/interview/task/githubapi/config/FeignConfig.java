package com.interview.task.githubapi.config;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer retryer(@Value("${feign.github.retry.period}") int period,
                           @Value("${feign.github.retry.maxPeriod}") int maxPeriod,
                           @Value("${feign.github.retry.maxAttempts}") int maxAttempts) {
        return new Retryer.Default(period, maxPeriod, maxAttempts);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new GithubErrorDecoder();
    }
}