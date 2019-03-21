package com.bevis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.TimeZone;

/**
 * Hello world!
 *
 * @author yanghuadong
 * @date 2019 -03-06
 */
@SpringBootApplication
@ComponentScan
public class FixClientApp {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(FixClientApp.class, args);
    }
}