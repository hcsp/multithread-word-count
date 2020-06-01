package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
    多个线程同时写入同一个Map来完成单词统计。
 */
public class MultiThreadWordCount2 {
    private static final Object lock = new Object();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        long t0 = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(threadNum);

        Map<String, Integer> finallyResult = new HashMap<>();

        for (File file : files) {
            pool.submit(new MultiThreadWordCount2.FileWordCount(file, lock, finallyResult));
        }
        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException("word count执行线程没有顺利结束", e);
        }
        long t1 = System.currentTimeMillis();
        System.out.println("MultiThreadWordCount2 cost " + (t1 - t0) + " ms");
        return finallyResult;
    }

    // 线程执行体，计算单个文件的wordCount
    static class FileWordCount implements Runnable {
        private final File file;
        private final Object lock;
        private final Map<String, Integer> finallyResult;

        FileWordCount(File file, Object lock, Map<String, Integer> finallyResult) {
            this.file = file;
            this.lock = lock;
            this.finallyResult = finallyResult;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    lineWordCount(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void lineWordCount(String line) {
            String[] words = line.split(" ");
            for (String word : words) {
                synchronized (lock) {
                    finallyResult.put(word, finallyResult.getOrDefault(word, 0) + 1);
                }
            }
        }


    }
}
