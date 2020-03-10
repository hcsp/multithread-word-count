package com.github.hcsp.multithread;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws IOException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        HashMap<String, Integer> resultMap = new HashMap<>();

        for (File file : files) {
            HashMap<String, Integer> OneFileResultMap = new HashMap<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<Future<Map<String, Integer>>> futureList = new ArrayList<>();

            for (int i = 0; i < 0; i++) {
                Future<Map<String, Integer>> future = threadPool.submit(new Callable<Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> call() throws Exception {
                        HashMap<String, Integer> oneLineMap = new HashMap<>();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            String[] words = line.split(" ");
                            for (String word : words) {
                                oneLineMap.put(word, oneLineMap.getOrDefault(word, 0) + 1);
                            }
                        }
                        return oneLineMap;
                    }
                });
                futureList.add(future);
            }
            mergeOneLineCountToOneFileCount(OneFileResultMap, futureList);
            mergeOneLineCountToOneFileCountByMap(resultMap, OneFileResultMap);
        }
        return resultMap;
    }

    private static void mergeOneLineCountToOneFileCountByMap(Map<String, Integer> resultMap,
                                                             Map<String, Integer> oneFileResultMap) {
        Set<String> keys = oneFileResultMap.keySet();
        for (String key : keys) {
            resultMap.put(key, oneFileResultMap.getOrDefault(key, oneFileResultMap.get(key)));
        }
    }

    private static void mergeOneLineCountToOneFileCount(HashMap<String, Integer> oneFileResultMap,
                                                        List<Future<Map<String, Integer>>> futureList) throws ExecutionException, InterruptedException {
        for (Future<Map<String, Integer>> futures : futureList) {
            Map<String, Integer> oneLineResult = futures.get();
            mergeOneLineCountToOneFileCountByMap(oneFileResultMap, oneLineResult);
        }
    }
}
