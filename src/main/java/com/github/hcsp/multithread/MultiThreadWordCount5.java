package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public class MultiThreadWordCount5 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        Map<String, Integer> wordCountMap = new HashMap<>();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> wordCountMapList = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("file is not found: " + file.getName());
            }
            wordCountMapList.add(threadPool.submit(new ReadFileWithCallable(reader)));

        }

        mergeResult(wordCountMap, wordCountMapList);
        threadPool.shutdown();
        return wordCountMap;
    }

    private static void mergeResult(Map<String, Integer> wordCountMap, List<Future<Map<String, Integer>>> wordCountMapList) {
        for (Future<Map<String, Integer>> mapFuture : wordCountMapList) {
            Set<Map.Entry<String, Integer>> entries = null;
            try {
                // future.get会等待线程执行完；
                entries = mapFuture.get().entrySet();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (entries != null) {
                for (Map.Entry<String, Integer> entry : entries) {
                    wordCountMap.put(entry.getKey(), wordCountMap.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }
        }
    }

    public static class ReadFileWithCallable implements Callable<Map<String, Integer>> {
        private BufferedReader reader;
        private Map<String, Integer> map = new HashMap<>();

        public ReadFileWithCallable(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            ReadFileUtils.readWordsToMap(map, reader);
            return map;
        }
    }
}
