package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileUtil {
    public static Map<String, Integer> count(File file){
        Map<String, Integer> freqMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String s = line.trim();
                String[] words = s.split("\\s+");
                for (String word : words) {
                    Integer count = freqMap.getOrDefault(word, 0) + 1;
                    freqMap.put(word, count);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return freqMap;
    }


    public static Map<String, Integer> merge(Map<String, Integer> target, Map<String, Integer> source) {
        Set<String> keys = target.keySet();
        for (String key : keys) {
            source.put(key, source.getOrDefault(key, 0) + target.get(key));
        }
        return source;
    }

}
