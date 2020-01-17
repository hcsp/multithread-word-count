package com.github.hcsp.multithread;


import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MultiThreadWordCount2 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    private static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < threadNum; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countFromEachFile(files, result);
                        countDownLatch.countDown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            countDownLatch.await();
        }
        return result;
    }

    private static void countFromEachFile(List<File> files, Map<String, Integer> result) throws IOException {
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
        }
    }
}











