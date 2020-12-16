package com.github.hcsp.multithread;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount5 {
    static final Object lock = new Object();
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> count = new ConcurrentHashMap<>();
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        long t0 = System.currentTimeMillis();
        for (File file : files) {
            new Thread(() -> {
                Map<String, Integer> map = GetWordInFile.getWordCountFormFile(file);
                synchronized (lock) {
                    GetWordInFile.mapAdd(count, map);
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println("time" + (System.currentTimeMillis() - t0));
        return count;
    }
}
