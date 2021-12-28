package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            futures.add(threadPool.submit(() -> {
                String line;
                Map<String, Integer> map = new HashMap<>();
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        map.put(word, map.getOrDefault(word, 0) + 1);
                    }
                }
                return map;
            }));
        }
        Map<String, Integer> resultMap = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> futureResult = future.get();
            for (Map.Entry<String, Integer> item : futureResult.entrySet()) {
                resultMap.put(item.getKey(), resultMap.getOrDefault(item.getKey(), 0) + item.getValue());
            }
        }

        return resultMap;
    }
}
