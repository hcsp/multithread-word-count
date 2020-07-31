package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {
    private static Map<String, Integer> finalMap = new LinkedHashMap<>();

    // (CountDownLatch) 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadNum);
        List<BufferedReader> readers = getReaders(files);
        Object lock = new Object();

        for (int i = 0; i < threadNum; i++) {
            new CountWord(readers, latch, lock).start();
        }
        latch.await();
        closeReaders(readers);
        return finalMap;
    }

    private static List<BufferedReader> getReaders(List<File> files) throws IOException {
        List<BufferedReader> readers = new ArrayList<>(files.size());
        for (File file : files) {
            readers.add(new BufferedReader(new FileReader(file)));
        }
        return readers;
    }

    public static class CountWord extends Thread {
        private List<BufferedReader> readers;
        private CountDownLatch latch;
        private final Object LOCK;

        public CountWord(List<BufferedReader> readers, CountDownLatch latch, Object lock) {
            this.readers = readers;
            this.latch = latch;
            this.LOCK = lock;
        }

        @Override
        public void run() {
            synchronized (LOCK) {
                try {
                    String s;
                    while ((s = getReadLine(readers)) != null) {
                        for (String word : s.split(" ")) {
                            if (!"".equals(word)) {
                                finalMap.put(word, finalMap.getOrDefault(word, 0) + 1);
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
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
