package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    //     使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Lock lock = new ReentrantLock();
        Condition allThreadFinished = lock.newCondition();
        List<Map<String, Integer>> mapList = new ArrayList<>();
        AtomicInteger len = new AtomicInteger(files.size());

        for (File file : files) {
            new Thread(() -> {
                lock.lock();
                try {
                    mapList.add(MultiThreadWordCount1.countOneFile(file));
                    if (len.decrementAndGet() == 0) {
                        allThreadFinished.signal();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }).start();
        }
        try {
            lock.lock();
            allThreadFinished.await();
        } finally {
            lock.unlock();
        }
        return MultiThreadWordCount1.merge(mapList);
    }
}
