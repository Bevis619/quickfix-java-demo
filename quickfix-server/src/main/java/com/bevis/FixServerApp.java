package com.bevis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import quickfix.ConfigError;

import java.util.TimeZone;

/**
 * Hello world!
 *
 * @author yanghuadong
 * @date 2019 -03-12
 */
@SpringBootApplication
@ComponentScan
public class FixServerApp {


    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws ConfigError the config error
     */
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(FixServerApp.class, args);
    }
}
