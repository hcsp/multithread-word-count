package com.github.hcsp.multithread;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量

    public static Map<String, Integer> count(int threadNum, List<File> files) throws ExecutionException, InterruptedException, FileNotFoundException {
        Map<String, Integer> finalFileMap = new HashMap<>();
        for (File file : files) {
            Map<String, Integer> map = countFile(threadNum, file);
            mergeWorkerResultIntoFinalResult(map, finalFileMap);
        }
        return finalFileMap;
    }

    public static Map<String, Integer> countFile(int threadNum, File file) throws FileNotFoundException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        List<Future<Map<String, Integer>>> list = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            list.add(threadPool.submit(new WorkJob(bufferedReader)));
        }
        Map<String, Integer> finalMap = new HashMap<>();
        for (Future<Map<String, Integer>> future : list) {
            Map<String, Integer> resultFromWorker = future.get();
            mergeWorkerResultIntoFinalResult(resultFromWorker, finalMap);
        }
        return finalMap;
    }

    static class WorkJob implements Callable<Map<String, Integer>> {

        private final BufferedReader bufferedReader;

        WorkJob(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> map = new HashMap<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] strings = line.split(" ");
                for (String string : strings) {
                    map.put(string, map.getOrDefault(string, 0) + 1);
                }
            }
            return map;
        }
    }

    private static void mergeWorkerResultIntoFinalResult(Map<String, Integer> resultFromWorker, Map<String, Integer> finalMap) {
        for (Map.Entry<String, Integer> entry : resultFromWorker.entrySet()) {
            int mergedResult = finalMap.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalMap.put(entry.getKey(), mergedResult);
        }
    }
}
