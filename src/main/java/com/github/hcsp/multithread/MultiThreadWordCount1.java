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

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {

        Map<String, Integer> finalResult = new HashMap<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<Future<Map<String, Integer>>> futures = getFutures(threadNum, reader);
            getFinalResult(finalResult, futures);
        }
        return finalResult;
    }

    public static void getFinalResult(Map<String, Integer> finalResult, List<Future<Map<String, Integer>>> futures) throws InterruptedException, ExecutionException {
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
    }

    private static List<Future<Map<String, Integer>>> getFutures(int threadNum, BufferedReader reader) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new WorkerJob(reader)));
        }
        return futures;
    }

    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                         Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader bufferedReader;

        WorkerJob(BufferedReader bufferedReader) {
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
