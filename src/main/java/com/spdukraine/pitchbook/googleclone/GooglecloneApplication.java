package com.spdukraine.pitchbook.googleclone;

import com.spdukraine.pitchbook.googleclone.config.SpringConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GooglecloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringConfig.class, args);
    }
}
