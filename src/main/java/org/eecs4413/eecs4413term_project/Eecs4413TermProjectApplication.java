package org.eecs4413.eecs4413term_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})

public class Eecs4413TermProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(Eecs4413TermProjectApplication.class, args);
    }

}
