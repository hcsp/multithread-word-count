package com.github.hcsp.multithread;


import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * lock condition.await()/signal() 方法
 */
public class MultiThreadWordCount2 {

    private static final Lock lock = new ReentrantLock();

    private static final Condition condition = lock.newCondition();

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

        lock.lock();
        try {
            if (counter.get() > 0) {
                condition.await();
            }
            for (Work work : workList) {
                FileUtil.merge(work.getFreqMap(), freqMap);
            }
        } finally {
            lock.unlock();
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
            this.freqMap = new HashMap<>();
        }

        @Override
        public void run() {
            lock.lock();
            try {
                freqMap = FileUtil.count(file);
                if (counter.decrementAndGet() == 0) {
                    condition.signal();
                }
            } finally {
                lock.unlock();
            }

        }
    }
}
