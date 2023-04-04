package com.azatkhaliullin.myenglishbot.awsTranslate;

import software.amazon.awssdk.services.translate.TranslateClient;

import java.util.concurrent.TimeoutException;

public interface ITranslator {

    /**
     * Создает объект запроса и выполняет перевод текста с помощью AWS Translate.
     *
     * @param translateClient клиент AWS Translate
     * @param source          язык исходного текста
     * @param target          язык целевого текста
     * @param text            текст для перевода
     * @return переведенный текст
     * @throws TimeoutException в случае, если не удалось получить перевод
     */
    String translate(TranslateClient translateClient,
                     Language source,
                     Language target,
                     String text) throws TimeoutException;

    /**
     * Доступные языки перевода
     */
    enum Language {
        RU("ru"), EN("en");

        /**
         * Код языка в AWS Translate
         */
        private final String awsTranslateValue;

        Language(String awsTranslateValue) {
            this.awsTranslateValue = awsTranslateValue;
        }

        public String getAwsTranslateValue() {
            return awsTranslateValue;
        }
    }

}
