package com.azatkhaliullin.myenglishbot.awsTranslate;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.DescribeVoicesRequest;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechResponse;
import software.amazon.awssdk.services.polly.model.Voice;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class AWSTranslator implements ITranslator {
    private final ThreadPoolExecutor executorService;
    private Voice voice = null;

    public AWSTranslator(ThreadPoolExecutor executor) {
        this.executorService = executor;
    }

    @Override
    public String translateText(TranslateClient translateClient,
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

    @Override
    public Voice pollyVoice(PollyClient pollyClient,
                            Language language) {
        if (voice == null) {
            DescribeVoicesRequest request = DescribeVoicesRequest.builder()
                    .engine("standard")
                    .languageCode(language.getAwsPollyValue())
                    .build();
            voice = pollyClient.describeVoices(request).voices().get(0);
        }
        return voice;
    }

    /**
     * Приватный метод, который используется для отправки задачи на перевод в пул потоков.
     *
     * @param source язык исходного текста
     * @param target язык целевого текста
     * @param text   текст для перевода
     * @return объект Future<String>, который содержит переведенный текст
     */
    private Future<String> submitTranslation(Language source,
                                             Language target,
                                             String text) {
        return executorService.submit(() -> {
            try (TranslateClient client = TranslateClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build()) {
                log.debug("Создан клиент AWS Translate");
                return translateText(client, source, target, text);
            } catch (Exception e) {
                log.error("Ошибка вызова AWS Translate", e);
                throw e;
            }
        });
    }

    public Future<byte[]> submitAudio(Language language,
                                      String text) {
        return executorService.submit(() -> {
            try (PollyClient client = PollyClient.builder()
                    .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                    .build()) {
                log.debug("Создан клиент AWS Polly");
                return synthesizeSpeech(client, language, text);
            } catch (Exception e) {
                log.error("Ошибка вызова AWS Polly", e);
                throw e;
            }
        });
    }

    private byte[] synthesizeSpeech(PollyClient client, Language language, String text) throws IOException {
        log.debug("Отправка запроса в AWS Polly");
        SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                .text(text)
                .languageCode(language.getAwsPollyValue())
                .voiceId(pollyVoice(client, language).id())
                .outputFormat(OutputFormat.MP3)
                .build();
        log.debug("Сформирован запрос в AWS Polly, {}", request);
        try (ResponseInputStream<SynthesizeSpeechResponse> inputStream = client.synthesizeSpeech(request)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int readBytes;
            byte[] buffer = new byte[1024];
            while ((readBytes = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }
            outputStream.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public byte[] getVoice(Language language,
                           String text) {
        try {
            return submitAudio(language, text).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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
    public String getTranslate(Language source,
                               Language target,
                               String text) {
        try {
            return submitTranslation(source, target, text).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
