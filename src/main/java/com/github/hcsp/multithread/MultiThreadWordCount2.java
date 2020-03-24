package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadWordCount2 {
    private static int count;
    private static final Object lock = new Object();

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

        synchronized (lock) {
            while (count > 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
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
            synchronized (lock) {
                if (--count == 0) {
                    lock.notify();
                }
            }
        }
    }
}
