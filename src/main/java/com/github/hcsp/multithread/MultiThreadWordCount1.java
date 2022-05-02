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
//    private final int threadNum;
//    private static ExecutorService threadPool;
//
//    public MultiThreadWordCount1(int threadNum) {
//        threadPool = Executors.newFixedThreadPool(threadNum);
//        this.threadNum = threadNum;
//    }

    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException, FileNotFoundException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> finalResult = new HashMap<>();
        for (File file: files) {
            List<Future<Map<String, Integer>>> futures = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new WorkerJob(reader)));

            }
            for (Future<Map<String, Integer>> future: futures) {
                Map<String, Integer> resultFromWorker = future.get();
                mergeResult(resultFromWorker, finalResult);
            }
        }

        return finalResult;
    }

    private static void mergeResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry: resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int number = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, number);
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



