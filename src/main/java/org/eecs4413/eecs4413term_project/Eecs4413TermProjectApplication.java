package org.eecs4413.eecs4413term_project;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication 
@EnableScheduling
public class Eecs4413TermProjectApplication {
    public static void main(String[] args) {
        // 1. Instantiate the SpringApplication
        SpringApplication application = 
            new SpringApplication(Eecs4413TermProjectApplication.class);
        application.run(args);
    }
}
