package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量

    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {

        ExecutorService threadpool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();


        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadpool.submit(() -> {
                    String line = null;
                    Map<String, Integer> result = new HashMap<>();
                    while ((line = reader.readLine()) != null) {
                        String[] words = line.split(" ");
                        for (String word : words) {
                            result.put(word, result.getOrDefault(word, 0) + 1);
                        }
                    }
                    return result;
                }));
            }
        }
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultsFromWorkers = future.get();
            mergeWorkerResultIntoFinalResult(resultsFromWorkers, finalResult);
        }
        return finalResult;
    }

    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultsFromWorkers,
                                                         Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultsFromWorkers.entrySet()) {
            String word = entry.getKey();
            int mergedResut = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResut);
        }
    }
}
