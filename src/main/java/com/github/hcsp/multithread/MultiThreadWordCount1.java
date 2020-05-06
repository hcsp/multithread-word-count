package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Object.wait/notify 实现
 *
 * @author kwer
 * @date 2020/5/5 18:18
 */
public class MultiThreadWordCount1 {
    private static LinkedBlockingQueue<File> queue = new LinkedBlockingQueue<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        Object lock = new Object();
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
                    synchronized (lock) {
                        fileSize.decrementAndGet();
                        lock.notify();
                    }
                    file = queue.poll();
                }
            }).start();
        }

        synchronized (lock) {
            while (fileSize.get() > 0) {
                lock.wait();
            }
        }

        for (Map<String, Integer> map : resultList) {
            finalResult = WordCountUtil.merge(finalResult, map);

        }
        return finalResult;
    }
}
