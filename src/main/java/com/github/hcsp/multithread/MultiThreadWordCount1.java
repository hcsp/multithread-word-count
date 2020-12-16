package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadWordCount1 {
    static final Object lock = new Object();

    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> count = new ConcurrentHashMap<>();
        AtomicInteger restFileNumber = new AtomicInteger(files.size());
        long t0 = System.currentTimeMillis();
        for (File file : files) {
            new Thread(() -> {
                Map<String, Integer> map = GetWordInFile.getWordCountFormFile(file);
                synchronized (lock) {
                    GetWordInFile.mapAdd(count, map);
                    if (restFileNumber.decrementAndGet() == 0) {
                        lock.notify();
                    }
                }
            }).start();
        }
        synchronized (lock) {
            while (restFileNumber.intValue() > 0) {
                lock.wait();
            }
        }
        System.out.println("time" + (System.currentTimeMillis() - t0));
        return count;
    }
}
