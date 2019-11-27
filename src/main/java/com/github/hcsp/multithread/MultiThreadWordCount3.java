package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用CountDownLatch实现多线程协作
 */
public class MultiThreadWordCount3 {
    public static void main(String[] args) {
        List<File> files = Arrays.asList(
                new File("1.txt"),
                new File("2.txt")
        );
        // count(files.size(), files);
        System.out.println(count(files.size(), files));
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ConcurrentHashMap<String, Integer> finalResultMap = new ConcurrentHashMap<>(); // 最终结果集
        CountDownLatch latch = new CountDownLatch(threadNum);

        for (File file : files) {
            new Thread(() -> {
                merge(finalResultMap, wordCount(file));
                latch.countDown();
            }).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return finalResultMap;
    }

    private static void merge(Map<String, Integer> map1, Map<String, Integer> map2) {
        for (String word : map2.keySet()) {
            map1.put(word,
                    map1.getOrDefault(word, 0) + map2.getOrDefault(word, 0));
        }
    }

    private static Map<String, Integer> wordCount(File file) {
        ConcurrentHashMap<String, Integer> wordToCountMap = new ConcurrentHashMap<>();

        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    int count = wordToCountMap.getOrDefault(word, 0);
                    wordToCountMap.put(word, count + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordToCountMap;
    }
}
