package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用 Lock/Condition 实现
 */
public class MultiThreadWordCount2 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition allThreadFinished = lock.newCondition();
        AtomicInteger count = new AtomicInteger(files.size());
        List<Map<String, Integer>> result = new CopyOnWriteArrayList<>();
        for (File file : files) {
            new Thread(() -> {
                try {
                    result.add(Util.countWordFromOneFile(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                lock.lock();
                try {
                    count.decrementAndGet();
                    allThreadFinished.signal();
                } finally {
                    lock.unlock();
                }
            }).start();
        }
        lock.lock();
        try {
            while (count.get() > 0) {
                allThreadFinished.await();
            }
        } finally {
            lock.unlock();
        }
        System.out.println(count.get());

        return Util.mergeMapsFromList(result);
    }
}
