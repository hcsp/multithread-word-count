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

public class MultiThreadWordCount1 {
    private static Map<String, Integer> finalMap = new LinkedHashMap<>();
    private static final Object LOCK = new Object();
    private static boolean finishRead = false;

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<BufferedReader> readers = getReaders(files);

        for (int i = 0; i < threadNum; i++) {
            threadPool.execute(new CountWord(readers));
        }

        synchronized (LOCK) {
            while (!finishRead) {
                LOCK.wait();
            }
        }

        threadPool.shutdown();
        closeReaders(readers);
        return finalMap;
    }

    private static List<BufferedReader> getReaders(List<File> files) throws FileNotFoundException {
        List<BufferedReader> readers = new ArrayList<>(files.size());
        for (File file : files) {
            readers.add(new BufferedReader(new FileReader(file)));
        }
        return readers;
    }

    public static class CountWord extends Thread {
        private final List<BufferedReader> readers;

        public CountWord(List<BufferedReader> readers) {
            this.readers = readers;
        }

        @Override
        public void run() {
            synchronized (LOCK) {
                String s;
                while (!finishRead && (s = getReadLine(readers)) != null) {
                    for (String word : s.split(" ")) {
                        if (!"".equals(word)) {
                            finalMap.put(word, finalMap.getOrDefault(word, 0) + 1);
                        }
                    }
                }
                LOCK.notify();
            }
        }
    }

    private static String getReadLine(List<BufferedReader> readers) {
        try {
            for (BufferedReader bReader : readers) {
                String line;
                if ((line = bReader.readLine()) != null) {
                    return line;
                }
            }
            finishRead = true;
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException when BufferedReader readLine!");
        }
    }

    private static void closeReaders(List<BufferedReader> readers) throws IOException {
        for (BufferedReader bReader : readers) {
            bReader.close();
        }
    }
}
