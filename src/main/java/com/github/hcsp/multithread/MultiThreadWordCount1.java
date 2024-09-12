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
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        Map<String, Integer> finalResult = new HashMap<>();

        for (File file : files) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            List<Future<Map<String, Integer>>> futures = new ArrayList<>();

            //创建线程
            for (int i = 0; i < threadNum; i++) {
                futures.add(threadPool.submit(new WorkerJob(bufferedReader)));
            }
            //遍历线程的结果并进行整合
            for (Future<Map<String, Integer>> future : futures) {
                Map<String, Integer> resultFromWorker = future.get();
                mergeWorkerResultIntoFinalResult(resultFromWorker, finalResult);
            }
        }
        return finalResult;
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {
        private BufferedReader bufferedReader;

        public WorkerJob(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            String content;
            Map<String, Integer> result = new HashMap<>();
            while ((content = bufferedReader.readLine()) != null) {
                String[] words = content.split(" ");
                for (String word : words) {
                    result.put(word, result.getOrDefault(word, 0) + 1);
                }
            }
            return result;
        }
    }

    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalResult) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            String word = entry.getKey();
            int mergedResult = finalResult.getOrDefault(word, 0) + entry.getValue();
            finalResult.put(word, mergedResult);
        }
    }
}
