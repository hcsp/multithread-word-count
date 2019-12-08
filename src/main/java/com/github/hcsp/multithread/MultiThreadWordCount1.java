package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 使用Future + 线程池 完成
 */
public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> countMap = new HashMap<>();

        files.forEach(file -> {
            try {
                for (int i = 0; i < threadNum; i++) {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    futures.add(threadPool.submit(new WorkerCount(bufferedReader)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        for (Future<Map<String, Integer>> future : futures) {
            try {
                mergeWorkerCount(future.get(), countMap);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return countMap;
    }

    private static void mergeWorkerCount(Map<String, Integer> workerCountResult,
                                         Map<String, Integer> finalCountResult) {
        for (Map.Entry<String, Integer> entry : workerCountResult.entrySet()) {
            String workerCountKey = entry.getKey();
            Integer workerCountValue = entry.getValue();
            Integer finalCountValue = finalCountResult.getOrDefault(workerCountKey, 0);
            finalCountResult.put(workerCountKey, workerCountValue + finalCountValue);
        }
    }

    private static class WorkerCount implements Callable<Map<String, Integer>> {
        BufferedReader reader;

        WorkerCount(BufferedReader bufferedReader) {
            this.reader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> countMap = new HashMap<>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] strArr = line.split(" ");
                for (String str : strArr) {
                    countMap.put(str, countMap.getOrDefault("str", 0) + 1);
                }
            }

            return countMap;
        }
    }
}
