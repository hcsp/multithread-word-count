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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    // ThreadPool
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {
        HashMap<String, Integer> fullResult = new HashMap<>();
        for (File file : files) {
            mergePartialResult(getPartialResultByThreadPool(threadNum, file), fullResult);
        }
        return fullResult;
    }

    private static List<Future<Map<String, Integer>>> getPartialResultByThreadPool(int threadNum, File file) throws FileNotFoundException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        for (int i = 0; i < threadNum; i++) {
            futures.add(executorService.submit(new WorkerJob(reader)));
        }
        return futures;
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        WorkerJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            HashMap<String, Integer> partialResult = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    partialResult.put(word, partialResult.getOrDefault(word, 0) + 1);
                }
            }

            return partialResult;
        }
    }

    private static void mergePartialResult(List<Future<Map<String, Integer>>> futures, Map<String, Integer> fullResult) throws ExecutionException, InterruptedException {
        for (Future<Map<String, Integer>> future : futures) {
            for (Map.Entry<String, Integer> entry : future.get().entrySet()) {
                String word = entry.getKey();
                fullResult.put(word, fullResult.getOrDefault(word, 0) + entry.getValue());
            }
        }
    }
}
