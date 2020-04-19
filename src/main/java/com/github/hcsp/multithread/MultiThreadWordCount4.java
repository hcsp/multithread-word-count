package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadWordCount3 {
    // 使用threadNum个线程，并发统计文件中各单词的数量 CountDownLatch
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, InterruptedException {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Integer>> middleList = new ArrayList<>();
        for (File file : files) {
            BufferedReader bReader = Files.newBufferedReader(file.toPath());
            ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
            CountDownLatch latch = new CountDownLatch(threadNum);
            for (int i = 0; i < threadNum; i++) {
                Map<String, Integer> middleResult = new HashMap<>();
                Runnable runnable = () -> {
                    try {
                        String line;
                        while ((line = bReader.readLine()) != null) {
                            String[] strings = line.split(" ");
                            for (String word : strings) {
                                middleResult.put(word, middleResult.getOrDefault(word, 0) + 1);
                            }
                        }
                        middleList.add(middleResult);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                };
                executorService.execute(runnable);
            }
            latch.await();
            for (Map<String, Integer> stringIntegerMap : middleList) {
                stringIntegerMap.forEach((key, value) -> {
                    result.put(key, result.getOrDefault(key, 0) + value);
                });
            }
//            bReader.close();
        }
        return result;
    }
}
