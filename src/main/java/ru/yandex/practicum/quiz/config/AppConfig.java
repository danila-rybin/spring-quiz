package ru.yandex.practicum.quiz.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter @Getter @ToString
@ConfigurationProperties("spring-quiz")
public class AppConfig {
    private String title;
}