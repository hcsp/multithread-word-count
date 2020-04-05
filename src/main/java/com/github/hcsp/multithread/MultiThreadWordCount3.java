package com.github.hcsp.multithread;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadWordCount3 {

    private static Map<String, Integer> totalResult = new HashMap<>();
    private static final Object lock = new Object();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        synchronized (lock) {
            System.out.println("Start counting...");
            while (!files.isEmpty()) {
                File file = files.remove(files.size() - 1);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                executorService.execute(new WordRunner(reader));
                lock.wait();
                System.out.println("Left files count " + files.size());
            }
        }

        System.out.println("All the files Counting Done");
        executorService.shutdown();
        return totalResult;
    }

    private static void mergeResult(Map<String, Integer> result) {
        synchronized (lock) {
            System.out.println("Merging result...");
            for (Map.Entry<String, Integer> entry : result.entrySet()) {
                int value = totalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
                totalResult.put(entry.getKey(), value);
            }
            lock.notify();
            System.out.println("Notify monitor");
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
