package org.eecs4413.eecs4413term_project;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})

public class Eecs4413TermProjectApplication {

    public static void main(String[] args) {
        System.out.println("Starting EECS4413 Term Project Application...");
        org.springframework.boot.SpringApplication.run(Eecs4413TermProjectApplication.class, args);
    }

}