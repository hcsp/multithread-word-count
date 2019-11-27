package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用Lock/Condition实现线程间协作
 */
public class MultiThreadWordCount2 {
    public static void main(String[] args) {
        List<File> files = Arrays.asList(
                new File("1.txt"),
                new File("2.txt")
        );
        // count(files.size(), files);
        System.out.println(count(files.size(), files));
    }

    static Lock lock = new ReentrantLock();
    static Condition merge = lock.newCondition();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ConcurrentHashMap<String, Integer> finalResultMap = new ConcurrentHashMap<>(); // 最终结果集
        Container container = new Container(threadNum);

        for (File file : files) {
            new Thread(() -> {
                lock.lock();
                try {
                    container.threadNum--;
                    merge(finalResultMap, wordCount(file));
                    if (container.threadNum == 0) {
                        merge.signal();
                    }
                } finally {
                    lock.unlock();
                }
            }).start();
        }

        lock.lock();
        while (container.threadNum != 0) {
            try {
                merge.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lock.unlock();

        return finalResultMap;
    }

    static class Container {
        int threadNum;

        public Container(int threadNum) {
            this.threadNum = threadNum;
        }
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
