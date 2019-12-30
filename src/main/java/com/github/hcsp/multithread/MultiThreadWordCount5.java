package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount5 {

    private static Map<String, Integer> wordCounts = new ConcurrentHashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        AtomicInteger count = new AtomicInteger(10);
        Lock lock = new ReentrantLock();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            File file = files.get(i);
            Thread thread = new Thread(() -> {
                lock.lock();
                try {
                    List<String> readAllLines = Files.readAllLines(Paths.get(file.getPath()));
                    for (String lines : readAllLines) {
                        String[] words = lines.split("\\s");
                        for (String word : words) {
                            Integer counts = wordCounts.getOrDefault(word, 0) + 1;
                            wordCounts.put(word, counts);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();

                }
            });
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }
        return wordCounts;
    }
}
