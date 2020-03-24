package com.github.hcsp.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<CountWordsWork> works = new ArrayList<>();
        List<Map<String, Integer>> results = new ArrayList<>();

        for (File file : files) {
            CountWordsWork countWordsJob = new CountWordsWork(file, countDownLatch);
            threadPool.submit(countWordsJob);
            works.add(countWordsJob);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (CountWordsWork work : works) {
            results.add(work.getResult());
        }

        return FileUtil.sumWords(results);
    }

    private static class CountWordsWork implements Runnable {
        private final File file;
        private final CountDownLatch countDownLatch;
        private Map<String, Integer> result;

        CountWordsWork(File file, CountDownLatch countDownLatch) {
            this.file = file;
            this.countDownLatch = countDownLatch;
        }

        public Map<String, Integer> getResult() {
            return result;
        }

        @Override
        public void run() {
            result = FileUtil.countWords(file);
            countDownLatch.countDown();
        }
    }
}
