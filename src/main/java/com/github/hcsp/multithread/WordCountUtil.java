package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author kwer
 * @date 2020/5/5 18:56
 */
public class WordCountUtil {
    public static Map<String, Integer> count(File file) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, Integer> countResult = new HashMap<>();
        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                countResult.put(word, countResult.getOrDefault(word, 0) + 1);
            }
        }
        return countResult;
    }

    public static Map<String, Integer> merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        HashSet<String> wordSet = new HashSet<>(map1.keySet());
        Map<String, Integer> finalResult = new HashMap<>();
        wordSet.addAll(map2.keySet());
        for (String word : wordSet) {
            finalResult.put(word, map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));
        }
        return finalResult;
    }
}
