package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount3 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(files.size());
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();
        for (File file : files) {
            new FileCountThread(file, latch, wordCount).start();
        }

        latch.await();
        return wordCount;
    }

    private static class FileCountThread extends Thread {
        private CountDownLatch latch;
        private final Map<String, Integer> wordCount;
        private File file;


        FileCountThread(File file, CountDownLatch latch, Map<String, Integer> wordCount) {
            this.latch = latch;
            this.wordCount = wordCount;
            this.file = file;
        }

        @Override
        public void run() {
            try {
                List<String> allLines = Files.readAllLines(file.toPath());
                for (String line : allLines) {
                    String[] words = line.split(" ");
                    Arrays.asList(words).forEach(word -> {
                        synchronized (wordCount) {
                            int count = wordCount.getOrDefault(word, 0);
                            wordCount.put(word, ++count);
                        }
                    });
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                // 通知主线程条件，该子线程任务已完成
                latch.countDown();
            }
        }
    }
}
