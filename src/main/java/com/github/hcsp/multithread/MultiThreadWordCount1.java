package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    private static int threadNum;
    private static ExecutorService threadPool;

    public MultiThreadWordCount1(int threadNum) {
        this.threadNum = threadNum;
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public Map<String, Integer> count(File file) throws FileNotFoundException, ExecutionException, InterruptedException {
        // 开若干个线程，每个线程去读取文件的一行内容，并将其中的单词统计结果返回。
        // 最后，主线程将工作线程返回的结果汇总在一起。
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new WorkerJob(reader)));
        }

        Map<String, Integer> finalResult = new HashMap<>();

        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
        return finalResult;
    }

    private static Map<String, Integer> mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
        return finalResult;
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
