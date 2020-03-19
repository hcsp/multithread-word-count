package com.github.hcsp.multithread;


import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.hcsp.multithread.FileUtil.merge;

/**
 * synchronized Object.wait()/notify() 方法
 */
public class MultiThreadWordCount1 {

    private static final Object lock = new Object();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> freqMap = new HashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        AtomicInteger counter = new AtomicInteger(files.size());
        List<Work> workList = new ArrayList<>();
        for (File file : files) {
            Work work = new Work(counter, file);
            executorService.submit(work);
            workList.add(work);
        }
        synchronized (lock) {
            if (counter.get() > 0) {
                lock.wait();
            }
            for (Work work : workList) {
                merge(work.getFreqMap(), freqMap);
            }

        }
        return freqMap;
    }

    static class Work implements Runnable {

        Map<String, Integer> freqMap;
        AtomicInteger counter;
        File file;

        Map<String, Integer> getFreqMap() {
            return freqMap;
        }

        Work(AtomicInteger counter, File file) {
            this.counter = counter;
            this.file = file;
        }

        @Override
        public void run() {
            synchronized (lock) {
                freqMap = FileUtil.count(file);
                if (counter.decrementAndGet() == 0) {
                    lock.notify();
                }
            }
        }
    }
}
