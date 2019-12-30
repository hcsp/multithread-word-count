package com.github.hcsp.multithread;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount1 {
    public static Map<String, Integer> wordCounts = new ConcurrentHashMap<>();
    private static CountDownLatch countDownLatch = new CountDownLatch(10);

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        for (int i = 0; i < threadNum; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        File file = files.get(finalI);
                        List<String> allLines = Files.readAllLines(Paths.get(file.getPath()));
                        for (String words : allLines) {
                            String[] wordspaces = words.split("\\s");
                            synchronized (MultiThreadWordCount1.class) {
                                for (String word : wordspaces) {
                                    Integer orDefault = wordCounts.getOrDefault(word, 0) + 1;
                                    wordCounts.put(word, orDefault);
                                }
                            }
                        }
                        countDownLatch.countDown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        countDownLatch.await();
        return wordCounts;
    }


}
