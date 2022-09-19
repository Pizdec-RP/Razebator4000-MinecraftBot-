package ru.justnanix.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindUtils {
    public static List<String> findStringsByRegex(String text, Pattern regex) {
        List<String> strings = new ArrayList<>();
        Matcher match = regex.matcher(text);

        while (match.find())
            strings.add(text.substring(match.start(), match.end()));

        return strings;
    }

    public static String findStringByRegex(String text, Pattern regex) {
        Matcher match = regex.matcher(text);

        if (match.find())
            return text.substring(match.start(), match.end());

        return null;
    }
}
