package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Object lock = new Object();
        AtomicInteger len = new AtomicInteger(files.size());
        List<Map<String, Integer>> mapList = new ArrayList<>();
        for (File file : files) {
            new Thread(() -> {
                synchronized (lock) {
                    try {
                        mapList.add(countOneFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (len.decrementAndGet() == 0) {
                            lock.notify();
                        }
                    }
                }
            }).start();
        }
        synchronized (lock) {
            lock.wait();
        }
        return merge(mapList);
    }

    public static Map<String, Integer> countOneFile(File file) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        List<String> lines = Files.readAllLines(file.toPath());
        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                int currentWordCount = result.getOrDefault(word, 0);
                result.put(word, currentWordCount + 1);
            }
        }
        return result;
    }

    public static Map<String, Integer> merge(List<Map<String, Integer>> source) {
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Integer> one : source) {
            for (String key : one.keySet()) {
                int count = result.getOrDefault(key, 0);
                result.put(key, count + one.get(key));
            }
        }
        return result;
    }

}
