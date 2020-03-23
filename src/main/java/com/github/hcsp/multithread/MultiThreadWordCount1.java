package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * 使用传统synchronized、wait、notify机制
 */
public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        Map<String, Integer> wordCount = new HashMap<>();
        List<Thread> workThreads = new ArrayList<>();
        for (File file : files) {
            Thread workThread = new FileCountThread(file, wordCount);
            workThread.start();
            workThreads.add(workThread);
        }

        for (Thread thread : workThreads) {
            thread.join();
        }

        return wordCount;
    }

    private static class FileCountThread extends Thread {
        private File file;
        private Map<String, Integer> wordCount;

        FileCountThread(File file, Map<String, Integer> wordCount) {
            this.file = file;
            this.wordCount = wordCount;
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

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
