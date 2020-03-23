package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    private static Lock lock = new ReentrantLock();
    private static Condition allWorkDone = lock.newCondition();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        AtomicInteger workThreadNum = new AtomicInteger(files.size());
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();
        for (File file : files) {
            new MultiThreadWordCount2.FileCountThread(file, wordCount, workThreadNum).start();
        }

        // 等到所以子线程完成任务，来通知它
        lock.lock();
        try {
            while (workThreadNum.get() > 0) {
                allWorkDone.await();
            }
        } finally {
            lock.unlock();
        }
        return wordCount;
    }

    private static class FileCountThread extends Thread {
        private File file;
        private Map<String, Integer> wordCount;
        private AtomicInteger workThreadNum;

        FileCountThread(File file, Map<String, Integer> wordCount, AtomicInteger workThreadNum) {
            this.file = file;
            this.wordCount = wordCount;
            this.workThreadNum = workThreadNum;
        }

        @Override
        public void run() {
            try {
                List<String> allLines = Files.readAllLines(file.toPath());
                boolean done = false;
                while (!done) {
                    boolean locked = lock.tryLock();
                    if (locked) {
                        try {
                            for (String line : allLines) {
                                String[] words = line.split(" ");
                                Arrays.asList(words).forEach(word -> {
                                    int count = wordCount.getOrDefault(word, 0);
                                    wordCount.put(word, ++count);
                                });
                            }
                        } finally {
                            // 通知主线程条件，该子线程任务已完成
                            workThreadNum.decrementAndGet();
                            allWorkDone.signal();
                            lock.unlock();
                            done = true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
