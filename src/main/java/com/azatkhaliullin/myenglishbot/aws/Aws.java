package com.azatkhaliullin.myenglishbot.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * The Aws class provides methods for interacting with Amazon Web Services (AWS).
 */
@Slf4j
public class Aws {

    private static final String BASE_URL = "http://ec2-34-201-216-252.compute-1.amazonaws.com/";
    private final RestTemplate restTemplate;

    public Aws(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Translates text from a source language to a target language using AWS Translate.
     *
     * @param source the language from which the translation is made.
     * @param target the language into which the translation is being made.
     * @param text   the text to be translated.
     * @return the translated text.
     */
    public String translate(Language source,
                            Language target,
                            String text) {
        String url = BASE_URL + "translate" +
                "?source=" + source.name() + "&target=" + target.name();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate.postForObject(url, text, String.class);
    }

    /**
     * Synthesizes text into an audio file using AWS Polly.
     *
     * @param target the language in which the text is to be dubbed.
     * @param text   the text to be dubbed.
     * @return an array of bytes containing the audio data of the converted speech.
     */
    public byte[] polly(Language target,
                        String text) {
        String url = BASE_URL + "polly" +
                "?target=" + target.name();
        return restTemplate.postForObject(url, text, byte[].class);
    }

}
