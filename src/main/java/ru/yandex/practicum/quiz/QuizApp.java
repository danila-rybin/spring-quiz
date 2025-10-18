package ru.yandex.practicum.quiz;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import ru.yandex.practicum.quiz.config.AppConfig;
import ru.yandex.practicum.quiz.model.QuizLog;
import ru.yandex.practicum.quiz.service.ConsoleUI;
import ru.yandex.practicum.quiz.service.ReportGenerator;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@ConfigurationPropertiesScan
public class QuizApp implements CommandLineRunner {
    private final ConsoleUI ui;
    private final ReportGenerator reportGenerator;
    private final AppConfig config;

    public static void main(String[] args) {
        SpringApplication.run(QuizApp.class, args);
    }

    @Override
    public void run(String... args) {
        log.debug("Приложение запускается со следующей конфигурацией:\n{}", config);
        QuizLog log = ui.startQuiz();
        reportGenerator.generate(log);
    }
}