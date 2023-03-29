package com.azatkhaliullin.myenglishbot.awsTranslate;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
public class AWSTranslator implements ITranslator {

    private static final AWSTranslator awsTranslator = null;
    private final ThreadPoolExecutor executorService;

    private AWSTranslator() {
        executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    public static AWSTranslator getInstance() {
        if (awsTranslator == null) {
            return new AWSTranslator();
        }
        return awsTranslator;
    }


    @Override
    public String translate(TranslateClient translateClient, Language source, Language target, String text) {
        TranslateTextRequest request = TranslateTextRequest.builder()
                .sourceLanguageCode(source.getAwsTranslateValue())
                .targetLanguageCode(target.getAwsTranslateValue())
                .text(text)
                .build();
        TranslateTextResponse response = translateClient.translateText(request);

        return response.translatedText();
    }

    private Future<String> getTranslationSubmit(Language source, Language target, String text) {
        return executorService.submit(() -> {
            try (TranslateClient client = TranslateClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build()) {
                log.debug("Создан клиент AWS Translate");
                return translate(client, source, target, text);
            } catch (Exception e) {
                log.error("Ошибка вызова AWS Translate", e);
                throw e;
            }
        });
    }

    public String translate(Language source, Language target, String text) {
        try {
            return getTranslationSubmit(source, target, text).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
