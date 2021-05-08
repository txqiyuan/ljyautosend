package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LjymessageApplication extends SpringBootServletInitializer {

    /**
     * 设置headless(false)
     * 否则会报java.awt.HeadlessException
     */
    public static void main(String[] args) {
        //SpringApplication.run(LjymessageApplication.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(LjymessageApplication.class);
        builder.headless(false).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.headless(false).sources(LjymessageApplication.class);
    }

}
