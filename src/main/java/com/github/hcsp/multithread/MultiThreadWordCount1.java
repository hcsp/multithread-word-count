package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    private static ExecutorService threadPool;

    public MultiThreadWordCount1(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);
        // 使用threadNum个线程，并发统计文件中各单词的数量
    }

    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, FileNotFoundException, ExecutionException {
        // 线程安全
        BufferedReader reader = new BufferedReader(new FileReader(files.get(1)));

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (int i = 0; i < threadNum; ++i) {
            futures.add(threadPool.submit(new WorkerJob(reader)));
        }

        List<Map<String, Integer>> resultsFromWorkers = new ArrayList<>();
        for (Future<Map<String, Integer>> future : futures) {
            resultsFromWorkers.add(future.get());
        }

        Map<String, Integer> finalResult = new HashMap<>();

        for (Future<Map<String, Integer>> future: futures) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
        }
        
        return finalResult;
    }

    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry: resultFromWorker.entrySet()) {
            int mergedResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), mergedResult);
        }
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader reader;

        WorkerJob(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String,Integer> call() throws Exception {
            String line = null;
            Map<String, Integer> result = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] words = line.split(" ");

                for (String word: words) {
                    result.put(word, result.getOrDefault(word, 1));
                }

            }
            return null;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        // 内部方法无论有多耗时，都会立刻返回
        Future<Integer> future1 = threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                System.out.println(0);
                return 0;
            }
        });
    }
}
