package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount3 {
    private static int count;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition readUnCompleted = lock.newCondition();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        count = files.size();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<CountWordsWork> works = new ArrayList<>();
        List<Map<String, Integer>> results = new ArrayList<>();

        for (File file : files) {
            CountWordsWork countWordsJob = new CountWordsWork(file);
            threadPool.submit(countWordsJob);
            works.add(countWordsJob);
        }

        lock.lock();
        try {
            while (count > 0) {
                try {
                    readUnCompleted.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            lock.unlock();
        }

        for (CountWordsWork work : works) {
            results.add(work.getResult());
        }

        return FileUtil.sumWords(results);
    }

    private static class CountWordsWork implements Runnable {
        private final File file;
        private Map<String, Integer> result;

        CountWordsWork(File file) {
            this.file = file;
        }

        public Map<String, Integer> getResult() {
            return result;
        }

        @Override
        public void run() {
            result = FileUtil.countWords(file);
            lock.lock();
            try {
                if (--count == 0) {
                    readUnCompleted.signal();
                }
            } finally {
                lock.unlock();
            }

        }
    }
}
