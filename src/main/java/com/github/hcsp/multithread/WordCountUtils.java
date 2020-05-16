package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordCountUtils {
    private WordCountUtils() {
    }

    public static Map<String, Integer> mergeMap(List<Map<String, Integer>> mapList) {
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Integer> map : mapList) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String word = entry.getKey();
                Integer wordShowCount = entry.getValue();
                if (result.containsKey(word)) {
                    result.put(word, result.get(word) + wordShowCount);
                    continue;
                }
                result.put(word, wordShowCount);
            }
        }
        return result;
    }

    public static Map<String, Integer> statisticsFileWordCount(File file) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}
