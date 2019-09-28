package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordCount {

    private final int nThreads;

    private final CountDownLatch countDownLatch;

    private final Map<String, Integer> result = Collections.synchronizedMap(new HashMap<>());

    private final ExecutorService executorService;

    public WordCount(int threadNum) {
        this.nThreads = threadNum;
        countDownLatch = new CountDownLatch(nThreads);
        executorService = Executors.newFixedThreadPool(nThreads);
    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> file) {
        int size = file.size();
        int n = size / nThreads;
        int rest = size % nThreads;
        int j = 0;
        int start = 0;
        for (int i = 0; i < nThreads; i++) {
            int currentTaskCount = (j++ < rest) ? 1 + n : n;
            executorService.execute(new WordCountTask(file.subList(start, start + currentTaskCount)));
            start += currentTaskCount;
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private class WordCountTask implements Runnable {

        private List<File> tasks;

        WordCountTask(List<File> tasks) {
            this.tasks = tasks;
        }

        @Override
        public void run() {
            for (File file : tasks) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        doCount(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            countDownLatch.countDown();
        }

        private void doCount(String line) {
            String[] strings = line.split(" ");
            for (String s : strings) {
                result.put(s, result.getOrDefault(s, 0) + 1);
            }
        }

    }
}
