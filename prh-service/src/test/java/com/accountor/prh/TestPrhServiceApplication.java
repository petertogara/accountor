package com.accountor.prh;

import org.springframework.boot.SpringApplication;

public class TestPrhServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(PrhServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
