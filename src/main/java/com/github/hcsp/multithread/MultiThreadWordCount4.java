package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.*;

public class MultiThreadWordCount4 {
    //Future and ThreadPool
    //每个线程完成任务,并返回Future值,形成一个List<Future<Map<String,Integer>>>, 最后统一处理
    private static final List<Future<Map<String, Integer>>> futures = new ArrayList<>();

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        final ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        int chunkNum = files.size() / threadNum;

        for (int i = 0; i < threadNum; i++) {
            int begin = i * chunkNum;
            int end = i != threadNum - 1 ? (i + 1) * chunkNum : files.size();
            futures.add(threadPool.submit(new ReadAndCount(files.subList(begin, end))));
        }
        threadPool.shutdown();
        return changeFutures();
    }

    private static Map<String, Integer> changeFutures() {
        Map<String, Integer> result = new HashMap<>();
        for (Future<Map<String, Integer>> future :
                MultiThreadWordCount4.futures) {
            try {
                mergeFutureIntoResult(future.get(), result);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private static void mergeFutureIntoResult(Map<String, Integer> stringIntegerMap, Map<String, Integer> result) {
        for (Map.Entry<String, Integer> map :
                stringIntegerMap.entrySet()) {
            result.put(map.getKey(), result.getOrDefault(map.getKey(), 0) + map.getValue());
        }
    }

    private static class ReadAndCount implements Callable<Map<String, Integer>> {

        private final List<File> inputFiles;

        ReadAndCount(List<File> inputFiles) {
            this.inputFiles = inputFiles;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> result = new HashMap<>();
            for (File file :
                    inputFiles) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        mergeLineIntoResult(line, result);
                    }
                }
            }
            return result;
        }

        private void mergeLineIntoResult(String line, Map<String, Integer> result) {
            String[] words = line.split(" ");
            for (String word :
                    words) {
                result.put(word, result.getOrDefault(word, 0) + 1);
            }
        }
    }
}
