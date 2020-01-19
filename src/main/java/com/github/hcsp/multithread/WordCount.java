package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WordCount {
    private final int threadNum;
    private ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        this.threadNum = threadNum;
    }

    public Map<String, Integer> count(File file) throws FileNotFoundException, ExecutionException, InterruptedException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new WorkerJob(reader)));
        }

        //合并
        Map<String, Integer> finalResult = new HashMap<>();
        for (Future<Map<String, Integer>> future :
                futures) {
            Map<String, Integer> resultFromWorker = future.get();

            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }

        return finalResult;
    }

    public static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker,
                                                        Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry :
                resultFromWorker.entrySet()) {
            String word = entry.getKey();

            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        WorkerJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
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
