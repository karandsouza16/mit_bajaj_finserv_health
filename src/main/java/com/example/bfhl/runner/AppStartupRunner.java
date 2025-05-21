package com.example.bfhl.runner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.bfhl.service.WebhookService;

@Component
public class AppStartupRunner implements CommandLineRunner {

    private final WebhookService webhookService;

    public AppStartupRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Override
    public void run(String... args) {
        webhookService.handleStartupProcess();
    }
}
