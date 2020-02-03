package com.github.hcsp.multithread;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.hcsp.multithread.Utils.countFile;
import static com.github.hcsp.multithread.Utils.mergeIntoFirstMap;

public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        AtomicInteger restFileNumber = new AtomicInteger(files.size());
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        for (File file : files) {
            new Thread(() -> {
                try {
                    Map<String, Integer> countResult = countFile(file);
                    try {
                        lock.lock();
                        mergeIntoFirstMap(result, countResult);
                        if (restFileNumber.decrementAndGet() == 0) {
                            condition.signalAll();
                        }
                    } finally {
                        lock.unlock();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }

        //这里和obect的wait和notify机制一样，只有先拿到锁，才能条件await,signall也一样
        try {
            lock.lock();
            while (restFileNumber.intValue() > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
        return result;
    }
}
