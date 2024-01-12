package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws InterruptedException, ExecutionException {
        List<Future<Map<String, Integer>>> futureTaskList = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            FutureTask<Map<String, Integer>> task = new FutureTask<>(new Worker(files.get(i)));
            futureTaskList.add(task);
            new Thread(task).start();
        }
        List<Map<String, Integer>> workerResults = new ArrayList<>();
        for (Future<Map<String, Integer>> task : futureTaskList) {
            workerResults.add(task.get());
        }
        return mergeWorkerResults(workerResults);
    }

    public static class Worker implements Callable<Map<String, Integer>> {
        private final File file;

        public Worker(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> result = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        result.put(word, result.getOrDefault(word, 0) + 1);
                    }
                }
            }
            return result;
        }
    }

    private static Map<String, Integer> mergeWorkerResults(List<Map<String, Integer>> workerResults) {
        Map<String, Integer> result = new HashMap<>();
        for (Map<String, Integer> workResult : workerResults) {
            for (Map.Entry<String, Integer> entry : workResult.entrySet()) {
                result.put(entry.getKey(), result.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
        return result;
    }
}
