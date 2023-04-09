package com.azatkhaliullin.myenglishbot.awsTranslate;

import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.Voice;
import software.amazon.awssdk.services.translate.TranslateClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface ITranslator {

    /**
     * Создает объект запроса и выполняет перевод текста с помощью AWS Translate.
     *
     * @param translateClient клиент AWS Translate
     * @param source          язык исходного текста
     * @param target          язык целевого текста
     * @param text            текст для перевода
     * @return переведенный текст
     */
    String translateText(TranslateClient translateClient,
                         Language source,
                         Language target,
                         String text);

    Voice pollyVoice(PollyClient pollyClient,
                     Language language);

    /**
     * Доступные языки перевода
     */
    enum Language {
        RU("ru", "ru-RU"), EN("en", "en-US");

        /**
         * Код языка в AWS Translate
         */
        private final String awsTranslateValue;
        private final String awsPollyValue;


        Language(String awsTranslateValue, String awsPollyValue) {
            this.awsTranslateValue = awsTranslateValue;
            this.awsPollyValue = awsPollyValue;
        }

        public String getAwsTranslateValue() {
            return awsTranslateValue;
        }

        public String getAwsPollyValue() {
            return awsPollyValue;
        }

        public static List<String> getLanguagePairs() {
            return Arrays.stream(Language.values())
                    .flatMap(l1 -> Arrays.stream(Language.values())
                            .filter(l2 -> l1 != l2)
                            .map(l2 -> l1.name() + "_" + l2.name()))
                    .collect(Collectors.toList());
        }
    }

}
