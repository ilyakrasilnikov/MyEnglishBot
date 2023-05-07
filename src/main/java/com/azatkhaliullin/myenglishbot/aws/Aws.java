package com.azatkhaliullin.myenglishbot.aws;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * The Aws class provides methods for interacting with Amazon Web Services (AWS).
 */
@Slf4j
public class Aws {

    @Value("${aws.BASE_URL}")
    private String BASE_URL;
    private final RestTemplate restTemplate;

    public Aws(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
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
        try {
            URI url = new URIBuilder(BASE_URL)
                    .setPath("/translate")
                    .addParameter("source", source.name())
                    .addParameter("target", target.name()).build();
            return restTemplate.postForObject(url, text, String.class);
        } catch (URISyntaxException e) {
            log.error("Error when constructing the url address ", e);
            throw new RuntimeException(e);
        }
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
        try {
            URI url = new URIBuilder(BASE_URL)
                    .setPath("/polly")
                    .addParameter("target", target.name()).build();
            return restTemplate.postForObject(url, text, byte[].class);
        } catch (URISyntaxException e) {
            log.error("Error when constructing the url address ", e);
            throw new RuntimeException(e);
        }
    }

}