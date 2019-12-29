package com.github.hcsp.multithread;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadWordCount3 {
    public static Map<String, Integer> count(int threadNum, List<File> files){
        Map<String, Integer> wordNum = new HashMap<>();
        Container container = new Container(threadNum);
        for (File file : files) {
            new Thread(() -> {
                Map<String, Integer> fileMap = count(file);
                synchronized (container){
                    container.threadNum--;
                    mergeMap(wordNum, fileMap);
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
        return wordNum;
    }
    static class Container {
        int threadNum;

        Container(int threadNum) {
            this.threadNum = threadNum;
        }
    }
    private static void mergeMap(Map<String, Integer> dest, Map<String, Integer> src) {
        for (String key : src.keySet()) {
            dest.put(key, dest.getOrDefault(key, 0) + src.getOrDefault(key, 0));
//            dest.put(key, dest.getOrDefault(key, 0) + src.get(key));
        }
    }
    private static Map<String, Integer> count(File file)  {
        Map<String, Integer> wordNumMap = new HashMap<>();
        List<String> lines = null;
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        try {
            lines = Files.readAllLines(file.toPath());
//            while ((line = reader.readLine()) != null) {
            for (String line : lines) {
                String[] words = line.split(" ");
                for (String word : words) {
                    wordNumMap.put(word, wordNumMap.getOrDefault(word, 0) + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordNumMap;
    }
}
