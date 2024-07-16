package com.project.core.utils;

public class StringUtil {

    public static String convertCamelToSnake(String camelCase) {
        StringBuilder snakeCase = new StringBuilder();
        char[] charArray = camelCase.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];

            if (Character.isUpperCase(c) || Character.isDigit(c)) {
                if (i > 0 && i < charArray.length - 1 && Character.isDigit(charArray[i + 1])) {
                    snakeCase.append('_').append(Character.toLowerCase(c));
                } else {
                    snakeCase.append('_').append(Character.toLowerCase(c));
                }
            } else {
                snakeCase.append(c);
            }
        }

        if (snakeCase.length() > 0 && snakeCase.charAt(0) == '_') {
            snakeCase.deleteCharAt(0);
        }

        return snakeCase.toString();
    }

    public static String format(String s, String... args) {
        StringBuilder sb = new StringBuilder(s);
        int index = 0;
        for (String arg : args) {
            index = sb.indexOf("{}", index);
            if (index != -1) {
                sb.replace(index, index + 2, arg);
                index += arg.length();
            } else {
                break;
            }
        }
        return sb.toString();
    }

    public static String normalizeUri(String uri) {
        return uri.replaceAll("/\\d+", "");
    }

}
