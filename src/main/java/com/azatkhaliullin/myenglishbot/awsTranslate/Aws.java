package com.azatkhaliullin.myenglishbot.awsTranslate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Slf4j
public class Aws {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "http://ec2-107-22-127-7.compute-1.amazonaws.com/";

    public String translate(Language source,
                            Language target,
                            String text) {
        String url = BASE_URL + "translate" +
                "?source=" + source.name() + "&target=" + target.name();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate.postForObject(url, text, String.class);
    }

    public byte[] polly(Language target,
                        String text) {
        String url = BASE_URL + "polly" +
                "?target=" + target.name();
        return restTemplate.postForObject(url, text, byte[].class);
    }

}
