package com.github.hcsp.multithread;


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.hcsp.multithread.CountFile.wordsCount;

public class MultiThreadWordCount6 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        CountDownLatch latch = new CountDownLatch(files.size());
        Lock lock = new ReentrantLock();
        for (File file : files) {
            threadPool.submit(() -> {
                try {
                    lock.lock();
                    Map<String, Integer> words = wordsCount(file);
                    for (Map.Entry<String, Integer> entry : words.entrySet()) {
                        result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
                    }
                } catch (Exception ignored) {

                } finally {
                    lock.unlock();
                    latch.countDown();
                }
                return result;
            });
        }
        latch.await();
        return result;
    }

}
