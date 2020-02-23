package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MultiThreadWordCount1 {
    private static int threadNum;
    private static ExecutorService es;

    public MultiThreadWordCount1(int threadNum, ExecutorService es) {
        this.threadNum = threadNum;
        this.es = es;
    }

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException, ExecutionException, InterruptedException {
        Map<String, Integer> finalResult = new HashMap<>();
        for (File file : files) {
            Map<String, Integer> each = eachFileFinalResult(file);
            merge(each, finalResult);
        }
        return finalResult;
    }

    private static Map<String, Integer> eachFileFinalResult(File file) throws FileNotFoundException, ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        Map<String, Integer> finalResult = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        for (int j = 0; j < threadNum; j++) {
            futures.add(es.submit(new workerJob(br)));
        }
        for (Future<Map<String, Integer>> future : futures) {
            Map<String, Integer> resultFromWorker = future.get();
            merge(resultFromWorker, finalResult);
        }
        return finalResult;
    }

    static class workerJob implements Callable<Map<String, Integer>> {
        private BufferedReader br;

        private workerJob(BufferedReader br) {
            this.br = br;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String line = null;
            Map<String, Integer> result = new HashMap<String, Integer>();
            while ((line = br.readLine()) != null) {
                String[] words = line.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return null;
        }
    }

    private static void merge(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergeResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergeResult);
        }
    }
}
