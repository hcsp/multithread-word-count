package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CopyOnWriteArrayList;


public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // 使用CountDownLatch

    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        List<Map<String, Integer>> mapList = new CopyOnWriteArrayList<>();
        Map<String, Integer> results = new HashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(threadNum);
        for (File file : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            pool.execute(new ReadFileRunnable(bufferedReader, mapList, countDownLatch));
        }
        try {
            countDownLatch.await();
            for (Map<String, Integer> map : mapList) {
                for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
                    String word = stringIntegerEntry.getKey();
                    int countNum = results.getOrDefault(word, 0) + stringIntegerEntry.getValue();
                    results.put(word, countNum);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
        return results;
    }

    public static class ReadFileRunnable implements Runnable {
        BufferedReader bufferedReader;
        List<Map<String, Integer>> mapList;
        CountDownLatch latch;

        public ReadFileRunnable(BufferedReader bufferedReader, List<Map<String, Integer>> mapList, CountDownLatch latch) {
            this.bufferedReader = bufferedReader;
            this.mapList = mapList;
            this.latch = latch;
        }

        @Override
        public void run() {
            Map<String, Integer> fileResults = new HashMap<>();
            String line = null;
            while (true) {
                try {
                    if ((line = bufferedReader.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] words = line.split(" ");
                for (String word : words) {
                    fileResults.put(word, fileResults.getOrDefault(word, 0) + 1);
                }
            }
            this.latch.countDown();
            mapList.add(fileResults);
        }
    }
}

