package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, FileNotFoundException {
        CountDownLatch latch = new CountDownLatch(1);
        WordCounter counter = new WordCounter(files.size(), latch);
        new Thread(counter).start();

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            executorService.execute(new WordRunner(reader, counter));
        }

        latch.await();
        return counter.totalResult;
    }

    public static class WordCounter implements Runnable {

        private CountDownLatch timer;
        private CountDownLatch latch;
        Map<String, Integer> totalResult;

        public WordCounter(int count, CountDownLatch latch) {
            this.timer = new CountDownLatch(count);
            this.totalResult = new HashMap<>();
            this.latch = latch;
        }

        public synchronized void mergeResult(Map<String, Integer> result) {
            System.out.println("Merging result...");
            for (Map.Entry<String, Integer> entry : result.entrySet()) {
                int value = totalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
                totalResult.put(entry.getKey(), value);
            }
            timer.countDown();
            System.out.println("Left files count " + timer.getCount());
        }

        @Override
        public void run() {
            try {
                System.out.println("Start counting...");
                timer.await();
                System.out.println("All the files Counting Done");
                latch.countDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class WordRunner implements Runnable {

        private WordCounter counter;
        private BufferedReader reader;

        public WordRunner(BufferedReader reader, WordCounter counter) {
            this.reader = reader;
            this.counter = counter;
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
                counter.mergeResult(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
