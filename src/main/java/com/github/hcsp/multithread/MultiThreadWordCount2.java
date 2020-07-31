package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadWordCount2 {
    private static Map<String, Integer> finalMap = new LinkedHashMap<>();
    private static boolean finishRead = false;
    private static Lock lock = new ReentrantLock();
    private static Condition isFinishedYet = lock.newCondition();
//    private static Condition isStillReading = lock.newCondition();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<BufferedReader> readers = getReaders(files);

        for (int i = 0; i < threadNum; i++) {
            threadPool.execute(new CountWord(readers));
        }

        lock.lock();
        try {
            while (!finishRead) {
                isFinishedYet.await();
            }
        } finally {
            lock.unlock();
        }

        threadPool.shutdown();
        return finalMap;
    }

    public static class CountWord extends Thread {
        private List<BufferedReader> readers;

        public CountWord(List<BufferedReader> readers) {
            this.readers = readers;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                String s;
                while (!finishRead && (s = getReadLine(readers)) != null) {
                    for (String word : s.split(" ")) {
                        if (!"".equals(word)) {
                            finalMap.put(word, finalMap.getOrDefault(word, 0) + 1);
                        }
                    }
                }
                isFinishedYet.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    private static List<BufferedReader> getReaders(List<File> files) throws FileNotFoundException {
        List<BufferedReader> readers = new ArrayList<>(files.size());
        for (File file : files) {
            readers.add(new BufferedReader(new FileReader(file)));
        }
        return readers;
    }

    private static String getReadLine(List<BufferedReader> readers) {
        try {
            if (!finishRead) {
                for (BufferedReader bReader : readers) {
                    String line;
                    if ((line = bReader.readLine()) != null) {
                        return line;
                    }
                }
                finishRead = true;
                for (BufferedReader bReader : readers) {
                    bReader.close();
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException when BufferedReader readLine!");
        }
    }
}
