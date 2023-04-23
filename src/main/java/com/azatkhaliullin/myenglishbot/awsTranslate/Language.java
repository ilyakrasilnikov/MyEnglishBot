package com.azatkhaliullin.myenglishbot.awsTranslate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Available languages.
 */
public enum Language {
    RU, EN;

    public static List<String> getLanguagePairs() {
        return Arrays.stream(Language.values())
                .flatMap(l1 -> Arrays.stream(Language.values())
                        .filter(l2 -> l1 != l2)
                        .map(l2 -> l1.name() + "_" + l2.name()))
                .collect(Collectors.toList());
    }

}

