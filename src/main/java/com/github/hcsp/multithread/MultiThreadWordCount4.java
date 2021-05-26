package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount4 {
    //     使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();

        for (File file : files) {
            futures.add(threadPool.submit(new WorkerJob(file)));
        }


        for (Future<Map<String, Integer>> future : futures) {
            mergeWorkerResultIntoFileResult(future.get(), finalResult);
        }
        return finalResult;
    }


    private static void mergeWorkerResultIntoFileResult(Map<String, Integer> resultFromWorkers,
                                                        Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorkers.entrySet()) {
            int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), mergedResult);
        }
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private final File file;

        WorkerJob(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }
}
