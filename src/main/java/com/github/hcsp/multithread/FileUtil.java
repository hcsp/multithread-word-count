package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {

    public static Map<String, Integer> countWords(File file) {
        Map<String, Integer> result = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Map<String, Integer> sumWords(List<Map<String, Integer>> list) {
        Map<String, Integer> finalResult = new HashMap<>();

        for (Map<String, Integer> result : list) {
            for (String key : result.keySet()) {
                finalResult.put(key, finalResult.getOrDefault(key, 0) + result.get(key));
            }
        }

        return finalResult;
    }

}
