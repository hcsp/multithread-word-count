package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> countResult = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            futures.add(executorService.submit(() -> {
                Map<String, Integer> result = new HashMap<>();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    String[] split = line.split(" ");
                    for (String word : split) {
                        if (result.containsKey(word)) {
                            result.put(word, result.get(word) + 1);
                        } else {
                            result.put(word, 1);
                        }
                    }
                }
                return result;
            }));
        }
        Map<String, Integer> waitforMerge;
        for (Future<Map<String, Integer>> future : futures) {
            waitforMerge = future.get();
            mergeResulttoFinal(waitforMerge, countResult);
        }
        return countResult;
    }

    private static void mergeResulttoFinal(Map<String, Integer> waitforMerge, Map<String, Integer> countResult) {
        for (Map.Entry<String, Integer> entry : waitforMerge.entrySet()) {
            String word = entry.getKey();
            int i = countResult.getOrDefault(word, 0) + entry.getValue();
            countResult.put(word, i);
        }
    }
}
