package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount5 {
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> finalResultMap = new HashMap<>();
        Container container = new Container(threadNum);
        for (File file : files) {
            new Thread(() -> {
                Map<String, Integer> fileMap = wordCount(file);
                synchronized (container) {
                    container.threadNum--;
                    merge(finalResultMap, fileMap);
                    if (container.threadNum == 0) {
                        container.notify();
                    }
                }
            }).start();
        }
        synchronized (container) {
            while (container.threadNum != 0) {
                try {
                    container.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return finalResultMap;
    }
    static class Container {
        int threadNum;

        Container(int threadNum) {
            this.threadNum = threadNum;
        }
    }

    private static void merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        for (String word : map2.keySet()) {
            map1.put(word, map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));
        }
    }
    private static Map<String, Integer> wordCount(File file) {
        Map<String, Integer> wordToCountMap = new HashMap<>();
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split(" ");
                for (String word : words) {
                    int count = wordToCountMap.getOrDefault(word, 0);
                    wordToCountMap.put(word, count + 1);
//                    wordToCountMap.put(word, wordToCountMap.getOrDefault(word, 0) + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordToCountMap;
    }
}
