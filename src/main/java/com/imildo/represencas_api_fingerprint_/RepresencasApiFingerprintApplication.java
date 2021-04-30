package com.imildo.represencas_api_fingerprint_;

import com.imildo.represencas_api_fingerprint_.controller.CheckDirectories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RepresencasApiFingerprintApplication {

    public static void main(String[] args) {
        new CheckDirectories();
        SpringApplication.run(RepresencasApiFingerprintApplication.class, args);
    }

}
