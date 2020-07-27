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
        Map<String, Integer> res = new HashMap<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<Future<Map<String, Integer>>> futures = getFutures(threadNum, reader);
            getTotalCount(res, futures);
        }
        return res;
    }

    private static void getTotalCount(Map<String, Integer> finalResult, List<Future<Map<String, Integer>>> list) throws ExecutionException, InterruptedException {
        for (Future future : list) {
            Map<String, Integer> resultFormWorker = (Map<String, Integer>) future.get();
            for (Map.Entry<String, Integer> entry : resultFormWorker.entrySet()) {
                finalResult.put(entry.getKey(), finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue());
            }
        }
    }

    private static List<Future<Map<String, Integer>>> getFutures(int threadNum, BufferedReader reader) {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> res = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            res.add(threadPool.submit(new WorkerJob(reader)));
        }
        return res;
    }

    static class WorkerJob implements Callable<Map<String, Integer>> {

        private BufferedReader bufferedReader;

        WorkerJob(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }


        @Override
        public Map<String, Integer> call() throws Exception {
            String lines;
            Map<String, Integer> res = new HashMap<>();
            while ((lines = bufferedReader.readLine()) != null) {
                String[] words = lines.split(" ");
                for (String word : words) {
                    res.put(word, res.getOrDefault(word, 0) + 1);
                }
            }
            return res;
        }
    }
}
