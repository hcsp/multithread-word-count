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

// Lock/Condition
public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Map<String, Integer> resultMap = new ConcurrentHashMap<>();
        AtomicInteger restFileNumber = new AtomicInteger(files.size());
        for (File file : files) {
            new Thread(() -> {
                try {
                    Map<String, Integer> map = Common.countOneFile(file);
                    try {
                        lock.lock();
                        Common.mergeToMap(resultMap, map);
                    } finally {
                        if (restFileNumber.decrementAndGet() == 0) {
                            condition.signal();
                        }
                        lock.unlock();
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
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

        return resultMap;
    }
}
