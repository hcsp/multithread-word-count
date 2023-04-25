package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    //    public static Map<String, Integer> count(int threadNum, List<File> files) {
    //        return null;
    //    }

    private static final ReentrantLock lock = new ReentrantLock();
    private static final Map<String, Integer> finalResult = new HashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        for (File file : files) {
            countSingleFile(file, finalResult);
        }
        return finalResult;
    }

    private static void countSingleFile(File file, Map<String, Integer> finalResult) {
        lock.lock();
        try {
            /* Critical section here */
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                countSingleLine(finalResult, line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
    private static void countSingleLine(Map<String, Integer> result, String line) {
        String[] words = line.split(" ");
        for (String word : words) {
            result.put(word, result.getOrDefault(word, 0) + 1);
        }
    }
}
