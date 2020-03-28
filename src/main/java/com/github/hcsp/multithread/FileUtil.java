package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileUtil {

    public static Map<String, Integer> work(File file) {
        Map<String, Integer> wordCount = new HashMap<>();
        BufferedReader reader;
        try {
            String line;
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String s = line.trim();
                String[] words = s.split("\\s+");
                for (String word : words) {
                    Integer count = wordCount.getOrDefault(word, 0) + 1;
                    wordCount.put(word, count);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("异常信息:" + e.getMessage());
        }
        return wordCount;
    }

    public static Map<String, Integer> merge(Map<String, Integer> target, Map<String, Integer> source) {
        Set<String> keys = target.keySet();
        for (String key : keys) {
            source.put(key, source.getOrDefault(key, 0) + target.get(key));
        }
        return source;
    }
}