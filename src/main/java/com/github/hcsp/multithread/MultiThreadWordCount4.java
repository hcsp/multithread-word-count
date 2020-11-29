package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount4 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    //    public static Map<String, Integer> count(int threadNum, List<File> files) {
    //        return null;
    //    }
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new WordCount(reader)));
            }
        }

        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> oneOfFinalResult = future.get();
            mergeResult(finalResult, oneOfFinalResult);
        }
        return finalResult;
    }

    private static void mergeResult(Map<String, Integer> finalResult, Map<String, Integer> oneOfFinalResult) {
        for (Map.Entry<String, Integer> entry : oneOfFinalResult.entrySet()) {
            String word = entry.getKey();
            Integer count = entry.getValue();
            finalResult.put(word, finalResult.getOrDefault(word, 0) + count);
        }
    }
}
