package com.azatkhaliullin.myenglishbot.awsTranslate;

import software.amazon.awssdk.services.translate.TranslateClient;

import java.util.concurrent.TimeoutException;

public interface ITranslator {

    /**
     * Значение таймаута перевода по умолчанию
     */
    long DEFAULT_TIMEOUT_MSEC = 3000;

    /**
     * @param source язык, с которого нужно перевести
     * @param target язык, на который нужно перевести
     * @param text   фраза/слово для перевода
     * @return переведённая фраза/слово
     * @throws TimeoutException - не удалось выполнить перевод за отведённое время
     */
    String translate(TranslateClient translateClient,
                     Language source, Language target, String text) throws TimeoutException;

    /**
     * Языки, доступные для перевода
     */
    enum Language {
        RU("ru"),
        EN("en");

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
