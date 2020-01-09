package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {

    //使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger(files.size());
        ReentrantLock lock = new ReentrantLock();
        Condition allThreadFinished = lock.newCondition();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> result = new HashMap<>();

        for (File file : files) {
            threadPool.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            synchronized (result) {
                                result.put(word, result.getOrDefault(word, 0) + 1);
                            }
                        }
                    }

                    try {
                        lock.lock();
                        atomicInteger.decrementAndGet();
                        allThreadFinished.signal();
                    } finally {
                        lock.unlock();
                    }
                    return null;
                }
            });
        }

        lock.lock();
        try {
            while (atomicInteger.get() > 0) {
                allThreadFinished.await();
            }
        } finally {
            lock.unlock();
        }

        return result;
    }
}
