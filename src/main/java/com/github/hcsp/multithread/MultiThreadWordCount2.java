package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock/Condition 实现
 *
 * @author kwer
 * @date 2020/5/5 19:32
 */
public class MultiThreadWordCount2 {
    private static LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        for (File file : files) {
            queue.put(file);
        }
        List<Map<String, Integer>> resultList = new ArrayList<>(files.size());
        AtomicInteger fileSize = new AtomicInteger(files.size());
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                File file = queue.poll();
                while (file != null) {
                    System.out.println(Thread.currentThread().getName() + " 执行文件解析……");
                    resultList.add(WordCountUtil.count(file));
                    lock.lock();
                    try {
                        fileSize.decrementAndGet();
                        condition.signal();
                    } finally {
                        lock.unlock();
                    }
                    file = queue.poll();
                }
            }).start();
        }
        lock.lock();
        try {
            while (fileSize.get() > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }

        for (Map<String, Integer> map : resultList) {
            finalResult = WordCountUtil.merge(finalResult, map);

        }
        return finalResult;
    }
}
