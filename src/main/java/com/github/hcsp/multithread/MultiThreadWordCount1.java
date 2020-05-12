package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreadWordCount1 {
    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            Future<Map<String, Integer>> futureFileContent = readFileWithThreadPool(file, executorService);
            futures.add(futureFileContent);
        }
        return parseFutureFileContents(futures);
    }

    private static Map<String, Integer> parseFutureFileContents(List<Future<Map<String, Integer>>> futures) {
        Map<String, Integer> result = new HashMap<>();
        for (Future<Map<String, Integer>> future : futures) {
            try {
                Map<String, Integer> singleFileResult = future.get();
                for (Map.Entry<String, Integer> entry : singleFileResult.entrySet()) {
                    String word = entry.getKey();
                    Integer wordShowCount = entry.getValue();
                    if (result.containsKey(word)) {
                        result.put(word, result.get(word) + wordShowCount);
                        continue;
                    }
                    result.put(word, wordShowCount);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Future<Map<String, Integer>> readFileWithThreadPool(File file, ExecutorService executorService) {
        return executorService.submit(new ReadFileTask(file));
    }

    static class ReadFileTask implements Callable<Map<String, Integer>> {
        File file;

        ReadFileTask(File file) {
            this.file = file;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            Map<String, Integer> result = new HashMap<>();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        result.put(word, result.getOrDefault(word, 0) + 1);
                    }
                }
                return result;
            }

        }
    }
}
