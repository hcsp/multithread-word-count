package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CountUtil {
    public static Map<String, Integer> getCountResultFromSingleFile(File file) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Map<String, Integer> countResult = new HashMap<>();
        String line;
        while (true) {
            try {
                if ((line = reader.readLine()) == null) {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] words = line.split(" ");
            for (String word : words) {
                countResult.put(word, countResult.getOrDefault(word, 0) + 1);
            }
        }
        return countResult;
    }

    public static void mergeSingleCountResultToFinalCountResult(Map<String, Integer> countResult,
                                                                Map<String, Integer> finalCountResult) {
        countResult.forEach((key, value) -> finalCountResult.merge(key, value, Integer::sum));
    }
}
