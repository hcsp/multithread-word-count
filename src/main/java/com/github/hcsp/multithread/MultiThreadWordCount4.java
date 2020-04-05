package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount4 {

    private static Map<String, Integer> totalResult = new HashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition notEmpty = lock.newCondition();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        lock.lock();
        try {
            System.out.println("Start counting...");
            while (!files.isEmpty()) {
                File file = files.remove(files.size() - 1);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                executorService.execute(new WordRunner(reader));
                notEmpty.await();
                System.out.println("Left files count " + files.size());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        executorService.shutdown();
        return totalResult;
    }

    private static void mergeResult(Map<String, Integer> result) {
        lock.lock();
        try {
            System.out.println("Merging start...");
            for (Map.Entry<String, Integer> entry : result.entrySet()) {
                int value = totalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
                totalResult.put(entry.getKey(), value);
            }
            notEmpty.signal();
            System.out.println("Merging end");
        } finally {
            lock.unlock();
        }
    }

    static class WordRunner implements Runnable {

        private BufferedReader reader;

        WordRunner(BufferedReader reader) {
            this.reader = reader;
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
                mergeResult(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
