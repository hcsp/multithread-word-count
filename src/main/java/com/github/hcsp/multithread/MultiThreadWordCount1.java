package com.github.hcsp.multithread;

import java.io.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadWordCount1 {

    private static Map<String, Integer> totalResult = new HashMap<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, FileNotFoundException {
        CountDownLatch timer = new CountDownLatch(files.size());

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            executorService.execute(new WordRunner(reader, timer));
        }

        System.out.println("Start counting...");
        timer.await();
        System.out.println("All the files Counting Done");
        executorService.shutdown();
        return totalResult;
    }

    private static synchronized void mergeResult(Map<String, Integer> result, CountDownLatch timer) {
        System.out.println("Merging result...");
        for (Map.Entry<String, Integer> entry : result.entrySet()) {
            int value = totalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            totalResult.put(entry.getKey(), value);
        }
        timer.countDown();
        System.out.println("Left files count " + timer.getCount());
    }

    static class WordRunner implements Runnable {

        private BufferedReader reader;
        private CountDownLatch timer;

        WordRunner(BufferedReader reader, CountDownLatch timer) {
            this.reader = reader;
            this.timer = timer;
        }

        @Override
        public void run() {
            try {
                String line;
                Map<String, Integer> result = new HashMap<>();
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        int count = result.getOrDefault(word, 0);
                        result.put(word, count + 1);
                    }
                }
                mergeResult(result, timer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
