package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.hcsp.multithread.FileUtil.merge;

// Lock/Condition
public class MultiThreadWordCount2 {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        List<WorkJob> workList = new ArrayList<>();
        Map<String, Integer> result = new HashMap<>();
        AtomicInteger count = new AtomicInteger(threadNum);
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        for (File file : files) {
            WorkJob work = new WorkJob(file, result, count);
            threadPool.submit(work);
            workList.add(work);
        }

        lock.lock();
        try {
            if (count.get() > 0) {
                condition.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        for (WorkJob work : workList) {
            merge(work.getResult(), result);
        }
        return result;
    }

    private static class WorkJob implements Runnable {
        private File file;
        private Map<String, Integer> result;
        private AtomicInteger count;

        Map<String, Integer> getResult() {
            return result;
        }

        WorkJob(File file, Map<String, Integer> result, AtomicInteger count) {
            this.file = file;
            this.result = result;
            this.count = count;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                result = FileUtil.work(file);
                if (count.decrementAndGet() == 0) {
                    condition.signal();
                }
            } finally {
                lock.unlock();
            }

        }
    }
}
