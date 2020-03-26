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
    public MultiThreadWordCount1() {
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        executeCountWordsJob(threadNum, files, threadPool, futures);
        return getFinalResult(futures);
    }

    private static Map<String, Integer> getFinalResult(List<Future<Map<String, Integer>>> futures) throws InterruptedException, ExecutionException {
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            for (String key : resultFromWorker.keySet()) {
                finalResult.put(key, finalResult.getOrDefault(key, 0) + resultFromWorker.get(key));
            }
        }
        return finalResult;
    }

    private static void executeCountWordsJob(int threadNum, List<File> files, ExecutorService threadPool, List<Future<Map<String, Integer>>> futures) throws FileNotFoundException {
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            futures.add(threadPool.submit(new CountWordsJob(reader)));
        }
    }

    static class CountWordsJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        CountWordsJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line;
            Map<String, Integer> map = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");

                for (String word : words) {
                    map.put(word, map.getOrDefault(word, 0) + 1);
                }
            }

            return map;
        }
    }
}
