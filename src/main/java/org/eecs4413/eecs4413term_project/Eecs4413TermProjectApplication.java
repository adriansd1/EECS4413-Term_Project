package org.eecs4413.eecs4413term_project;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication 
@EnableScheduling
public class Eecs4413TermProjectApplication {
    public static void main(String[] args) {
        // 1. Instantiate the SpringApplication
        SpringApplication application = 
            new SpringApplication(Eecs4413TermProjectApplication.class);
        application.run(args);
    }

    // --- ADDED THIS METHOD TO FIX THE FRONTEND CONNECTION ---
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allows React (localhost:3000) to talk to this Backend
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}