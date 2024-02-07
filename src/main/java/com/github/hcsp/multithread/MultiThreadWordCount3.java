package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount3 {
    //使用CountDownLatch, 预备一个开始锁,和一个完成锁.
    private static final CountDownLatch startSignal = new CountDownLatch(1);
    private static final Map<String, Integer> result = new HashMap<>();
    private static CountDownLatch doneSignal;
    //用以互斥访问给定的文件
    private static final ReentrantLock lock = new ReentrantLock();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        doneSignal = new CountDownLatch(threadNum);
        int chunkNum = files.size() / threadNum;
        for (int i = 0; i < threadNum; i++) {
            int begin = i * chunkNum;
            int end = i != threadNum - 1 ? (i + 1) * chunkNum : files.size();
            List<File> inputFiles = files.subList(begin, end);
            new Thread(new ReadAndCount(inputFiles)).start();
        }
        startSignal.countDown();
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private static class ReadAndCount implements Runnable {
        private final List<File> inputFiles;

        public ReadAndCount(List<File> inputFiles) {
            this.inputFiles = inputFiles;
        }

        @Override
        public void run() {
            for (File file :
                    inputFiles) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        try {
                            startSignal.await();
                            lock.lock();
                            try {
                                mergeIntoResult(line);
                            } finally {
                                lock.unlock();
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    doneSignal.countDown();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void mergeIntoResult(String line) {
            String[] words = line.split(" ");
            for (String word :
                    words) {
                result.put(word, result.getOrDefault(word, 0) + 1);
            }
        }
    }
}
