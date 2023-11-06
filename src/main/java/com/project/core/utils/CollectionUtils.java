package com.project.core.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.project.core.exception.throwable.AppException;

public class CollectionUtils {

    public static <T> List<T> stringToList(String arrayAsString, Class<T> type) throws AppException {
        try{
            if (arrayAsString.equals("[]")) {
                List<T> list = new ArrayList<>();
                return list;
            }
            if (arrayAsString.startsWith("[") && arrayAsString.endsWith("]")) {
                List<String> splits = Arrays
                        .asList(arrayAsString.substring(1, arrayAsString.length() - 1).trim().split(","));

                List<T> list = splits.stream()
                        .map(s -> {
                            try {
                                return type.getConstructor(String.class).newInstance(s.trim());
                            } catch (Exception e) {
                                throw new IllegalArgumentException("Failed to convert string to type", e);
                            }
                        })
                        .toList();

                return list;
            }else{
                return new ArrayList<>();
            }
        }
        catch (Exception e) {
            throw new AppException("Failed to validate '" + type.getSimpleName() + "' array",e,400,"Verify elements type (required " + type.getSimpleName()+ ")");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] stringToArray(String arrayAsString, Class<T> type) throws AppException {
        try {
            if (arrayAsString.equals("[]")) {
                return (T[]) Array.newInstance(type, 0);
            }
            if (arrayAsString.startsWith("[") && arrayAsString.endsWith("]")) {
                String[] splits = arrayAsString.substring(1, arrayAsString.length() - 1).trim().split(",");

                T[] array = Arrays.stream(splits)
                        .map(s -> {
                            try {
                                return type.getConstructor(String.class).newInstance(s.trim());
                            } catch (Exception e) {
                                throw new IllegalArgumentException("Failed to convert string to type", e);
                            }
                        })
                        .toArray(size -> (T[]) Array.newInstance(type, size));

                return array;
            }else{
                return (T[]) Array.newInstance(type, 0);
            }
        } catch (Exception e) {
            throw new AppException("Failed to validate '" + type.getSimpleName() + "' array",e,400,"Verify elements type (required " + type.getSimpleName()+ ")");
        }

    }

}
