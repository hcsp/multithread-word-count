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
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> finalResult = new HashMap<>();

        for (File file : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            List<Future<Map<String, Integer>>> futures = new ArrayList<>();

            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new Worker(bufferedReader))); // 收集所有的结果
            }

            Map<String, Integer> eachResult = new HashMap<>();
            for (Future<Map<String, Integer>> future : futures) {
                Map<String, Integer> workResult = future.get();
                meargeWorkerResultToFinalResult(workResult, eachResult);
            }
            meargeWorkerResultToFinalResult(eachResult, finalResult);
        }
        return finalResult;
    }

    private static void meargeWorkerResultToFinalResult(Map<String, Integer> workResult, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : workResult.entrySet()) {
            int count = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), count);
        }
    }


    static class Worker implements Callable<Map<String, Integer>> {
        BufferedReader bufferedReader;

        Worker(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}
