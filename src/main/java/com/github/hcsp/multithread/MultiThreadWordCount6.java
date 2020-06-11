package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount6 {
    static final ReentrantLock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> count = new ConcurrentHashMap<>();
        AtomicInteger restFileNumber = new AtomicInteger(files.size());
        long t0 = System.currentTimeMillis();
        for (File file : files) {
            new Thread(() -> {
                Map<String, Integer> map = GetWordInFile.getWordCountFormFile(file);
                try {
                    lock.lock();
                    GetWordInFile.mapAdd(count, map);
                    if (restFileNumber.decrementAndGet() == 0) {
                        condition.signalAll();
                    }
                } finally {
                    lock.unlock();
                }
            }).start();
        }
        try {
            lock.lock();
            while (restFileNumber.intValue() > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }

        System.out.println("time" + (System.currentTimeMillis() - t0));
        return count;
    }
}
