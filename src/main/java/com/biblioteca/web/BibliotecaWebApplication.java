// BibliotecaWebApplication.java
package com.biblioteca.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BibliotecaWebApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BibliotecaWebApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaWebApplication.class, args);
    }
}