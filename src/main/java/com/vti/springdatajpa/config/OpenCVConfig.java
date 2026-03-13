package com.vti.springdatajpa.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenCVConfig {

    static {
        nu.pattern.OpenCV.loadLocally();
    }
}
