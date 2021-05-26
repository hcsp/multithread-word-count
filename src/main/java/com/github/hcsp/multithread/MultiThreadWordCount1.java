package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount1 {
    private static LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        ReentrantLock locker = new ReentrantLock();
        Condition condition = locker.newCondition();
        for (File file : files) {
            queue.put(file);
        }

        List<Map<String, Integer>> resultList = new ArrayList<>(files.size());
        AtomicInteger fileSize = new AtomicInteger(files.size());
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                File file = queue.poll();
                while (file != null) {
                    resultList.add(FileUtil.count(file));
                    locker.lock();
                    try {
                        fileSize.decrementAndGet();
                        condition.signal();
                    } finally {
                        locker.unlock();
                    }
                    file = queue.poll();
                }
            }).start();
        }
        locker.lock();
        try {
            while (fileSize.get() > 0) {
                condition.await();
            }
        } finally {
            locker.unlock();
        }

        for (Map<String, Integer> map : resultList) {
            finalResult = FileUtil.merge(finalResult, map);

        }
        return finalResult;
    }



}
