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
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> result = new HashMap<>();

        for (File file : files) {
            List<Future<Map<String, Integer>>> futures = getFileFutures(threadNum, threadPool, file);
            appendToResult(futures, result);
        }

        return result;
    }

    private static void appendToResult(List<Future<Map<String, Integer>>> futures, Map<String, Integer> result) throws ExecutionException, InterruptedException {
        for (Future<Map<String, Integer>> future : futures) {
            for (Map.Entry<String, Integer> entry : future.get().entrySet()) {
                String key = entry.getKey();
                result.put(key, result.getOrDefault(key, 0) + entry.getValue());
            }
        }
    }

    private static List<Future<Map<String, Integer>>> getFileFutures(int threadNum, ExecutorService threadPool, File file) throws FileNotFoundException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        for (int i = 0; i < threadNum; i++) {
            futures.add(threadPool.submit(new FileWork(bufferedReader)));
        }
        return futures;
    }

    public static class FileWork implements Callable {
        private BufferedReader bufferedReader;

        FileWork(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public Object call() throws Exception {
            String line = bufferedReader.readLine();
            Map<String, Integer> result = new HashMap<>();

            while (line != null) {
                String[] words = line.split(" ");
                for (String s : words) {
                    result.put(s, result.getOrDefault(s, 0) + 1);
                }

                line = bufferedReader.readLine();
            }

            return result;
        }
    }
}
