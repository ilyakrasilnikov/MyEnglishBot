package com.azatkhaliullin.myenglishbot.aws;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Language enum represents the available languages for translation and synthesis in the Aws class.
 */
public enum Language {
    /**
     * RU - Russian language.
     * EN - English language.
     */
    RU, EN;

    /**
     * Returns a list of all possible language pairs.
     *
     * @return a list of all possible language pairs.
     */
    public static List<String> getLanguagePairs() {
        return Arrays.stream(Language.values())
                .flatMap(l1 -> Arrays.stream(Language.values())
                        .filter(l2 -> l1 != l2)
                        .map(l2 -> l1.name() + "_" + l2.name()))
                .collect(Collectors.toList());
    }

}

