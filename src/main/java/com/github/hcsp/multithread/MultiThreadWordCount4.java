package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static ExecutorService executorService;
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        executorService = Executors.newFixedThreadPool(threadNum);
        return count(files);
    }
    // 统计文件中各单词的数量
    public static Map<String, Integer> count(List<File> files) {
        Map<String, Integer> wordNum = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new LinkedList<>();
        for (File file : files) {
            Future<Map<String, Integer>> future = executorService.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() throws Exception {
                    return count(file);
                }
            });
            futures.add(future);
        }
        for (Future<Map<String, Integer>> future : futures) {
            try {
                mergeMap(wordNum, future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return wordNum;
    }

    private static void mergeMap(Map<String, Integer> dest, Map<String, Integer> src) {
        for (String key : src.keySet()) {
            dest.put(key, dest.getOrDefault(key, 0) + src.get(key));
        }
    }

    public static Map<String, Integer> count(File file) throws IOException {
        Map<String, Integer> wordNumMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    wordNumMap.put(word, wordNumMap.getOrDefault(word, 0) + 1);
                }
            }
            return wordNumMap;
        }
    }
}
