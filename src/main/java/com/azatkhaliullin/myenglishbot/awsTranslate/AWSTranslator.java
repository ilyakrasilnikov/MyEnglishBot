package com.azatkhaliullin.myenglishbot.awsTranslate;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
public class AWSTranslator implements ITranslator {
    private final ThreadPoolExecutor executorService;

    public AWSTranslator(ThreadPoolExecutor executor) {
        this.executorService = executor;
    }

    @Override
    public String translate(TranslateClient translateClient,
                            Language source,
                            Language target,
                            String text) {
        TranslateTextRequest request = TranslateTextRequest.builder()
                .sourceLanguageCode(source.getAwsTranslateValue())
                .targetLanguageCode(target.getAwsTranslateValue())
                .text(text)
                .build();
        TranslateTextResponse response = translateClient.translateText(request);

        return response.translatedText();
    }

    /**
     * Приватный метод, который используется для отправки задачи на перевод в пул потоков.
     *
     * @param source язык исходного текста
     * @param target язык целевого текста
     * @param text   текст для перевода
     * @return объект Future<String>, который содержит переведенный текст
     */
    private Future<String> getTranslationSubmit(Language source,
                                                Language target,
                                                String text) {
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

    /**
     * Метод для перевода текста в асинхронном режиме.
     * Если возникают ошибки при выполнении задачи, то метод выбрасывает RuntimeException.
     *
     * @param source язык исходного текста
     * @param target язык целевого текста
     * @param text   текст для перевода
     * @return переведенный текст
     */
    public String translate(Language source,
                            Language target,
                            String text) {
        try {
            return getTranslationSubmit(source, target, text).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
