package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ReaderUtils {
    public static HashMap<String, Integer> readFileAsMap(BufferedReader bf) throws IOException {
        HashMap<String, Integer> wordsMap = new HashMap<>();
        String line;
        while ((line = bf.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                wordsMap.put(word, wordsMap.getOrDefault(word, 0) + 1);
            }
        }
        return wordsMap;
    }

    public static void readFileToConcurrencyMap(BufferedReader bf, ConcurrentHashMap<String, Integer> concurrentHashMap) throws IOException {
        String line;
        while ((line = bf.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                synchronized (ReaderUtils.class) {
                    concurrentHashMap.put(word, concurrentHashMap.getOrDefault(word, 0) + 1);
                }
            }
        }
    }

    public static void mergeMapToMap(HashMap<String, Integer> map, HashMap<String, Integer> finalMap) {
        map.forEach((key, val) -> {
            finalMap.put(key, finalMap.getOrDefault(key, 0) + val);
        });
    }

    public static HashMap<String, Integer> readFileAsMap(File file) throws IOException {
        HashMap<String, Integer> wordsMap = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                wordsMap.put(word, wordsMap.getOrDefault(word, 0) + 1);
            }
        }
        return wordsMap;
    }

    public static void readFilesToMap(List<File> files, ConcurrentHashMap<String, Integer> concurrentHashMap) {
        files.forEach(file -> {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                readFileToConcurrencyMap(br, concurrentHashMap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
