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
        Map<String, Integer> finalResult = new HashMap<>();
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);

        for (File file : files) {
            futures.add(threadPool.submit(new WorkerJob(file)));
        }

        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }

        return finalResult;
    }

    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                         Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), mergedResult);
        }
    }

    public static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        public WorkerJob(File reader) throws FileNotFoundException {
            this.reader = new BufferedReader(new FileReader(reader));
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line = null;
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
