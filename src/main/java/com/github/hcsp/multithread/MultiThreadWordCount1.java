package com.github.hcsp.multithread;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

/**
 * 使用{@link Future}与线程池实现多线程的WordCount
 */
public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        for (File file : files) {
            futures.add(service.submit(() -> countSingleFile(file)));
        }

        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> wordCount = future.get();
            merge(finalResult, wordCount);
        }

        service.shutdown();
        return finalResult;
    }

    /**合并两个map的结果到第一个map中*/
    protected static void merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        for (String word : map2.keySet()) {
            map1.put(word,
                    map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));
        }
    }

    /**统计单个文件的单词个数*/
    protected static Map<String, Integer> countSingleFile(File file) {
        Map<String, Integer> wordCount = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    wordCount.put(word, 1 + wordCount.getOrDefault(word, 0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordCount;
    }
}
