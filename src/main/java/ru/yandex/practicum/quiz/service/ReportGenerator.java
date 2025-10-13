package ru.yandex.practicum.quiz.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.quiz.config.AppConfig;
import ru.yandex.practicum.quiz.model.QuizLog;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.quiz.config.AppConfig.ReportMode.VERBOSE;
import static ru.yandex.practicum.quiz.config.AppConfig.ReportOutputMode.CONSOLE;

@Component
public class ReportGenerator {

    private final String reportTitle;
    private final AppConfig.ReportSettings reportSettings;

    public ReportGenerator(AppConfig appConfig) {
        this.reportTitle = appConfig.getTitle();
        this.reportSettings = appConfig.getReport();
    }

    public void generate(QuizLog quizLog) {
        // Если генерация отчёта отключена, ничего не делаем
        if (reportSettings == null || !reportSettings.isEnabled()) {
            return;
        }

        AppConfig.ReportOutputSettings outputSettings = reportSettings.getOutput();
        boolean isConsole = outputSettings == null || outputSettings.getMode() == CONSOLE;
        String path = outputSettings != null ? outputSettings.getPath() : null;

        try {
            if (!isConsole && path != null) {
                // Создаём родительскую директорию, если её нет
                File file = new File(path);
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
            }

            try (PrintWriter writer = isConsole ? new PrintWriter(System.out) : new PrintWriter(path)) {
                write(quizLog, writer);
            }
        } catch (Exception e) {
            System.out.println("При генерации отчёта произошла ошибка: " + e.getMessage());
        }
    }

    private void write(QuizLog quizLog, PrintWriter writer) {
        writer.println("Отчёт о прохождении теста " + reportTitle + "\n");

        for (QuizLog.Entry entry : quizLog) {
            if (reportSettings.getMode() == VERBOSE) {
                writeVerbose(writer, entry);
            } else {
                writeConcise(writer, entry);
            }
        }

        writer.printf("Всего вопросов: %d\nОтвечено правильно: %d\n",
                quizLog.total(), quizLog.successful());
    }

    private void writeVerbose(PrintWriter writer, QuizLog.Entry entry) {
        writer.println("Вопрос " + entry.getNumber() + ": " + entry.getQuestion().getText());

        List<String> options = entry.getQuestion().getOptions();
        for (int i = 0; i < options.size(); i++) {
            writer.println((i + 1) + ") " + options.get(i));
        }

        writer.print("Ответы пользователя: ");
        entry.getAnswers().forEach(a -> writer.print(a + " "));
        writer.println();

        writer.println("Содержит правильный ответ: " + (entry.isSuccessful() ? "да" : "нет"));
        writer.println();
    }

    private void writeConcise(PrintWriter writer, QuizLog.Entry entry) {
        char successSign = entry.isSuccessful() ? '+' : '-';
        String answers = entry.getAnswers().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
        writer.printf("%d(%s): %s\n", entry.getNumber(), successSign, answers);
    }
}
