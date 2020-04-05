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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiThreadWordCount2 {

    // 使用threadNum个线程，并发统计文件中各单词的数量
    public static Map<String, Integer> count(int threadNum, List<File> files) throws FileNotFoundException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        Map<String, Integer> wordsCount = new HashMap<>();

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();
        for (File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            futures.add(executorService.submit(new WordRunner(reader)));
        }

        for (Future<Map<String, Integer>> future : futures) {
            mergeWordsCount(wordsCount, future);
        }

        executorService.shutdown();
        return wordsCount;
    }

    private static void mergeWordsCount(Map<String, Integer> total, Future<Map<String, Integer>> future) {
        try {
            Map<String, Integer> result = future.get();
            for (Map.Entry<String, Integer> entry : result.entrySet()) {
                int value = total.getOrDefault(entry.getKey(), 0) + entry.getValue();
                total.put(entry.getKey(), value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static class WordRunner implements Callable<Map<String, Integer>> {

        private BufferedReader reader;

        WordRunner(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public Map<String, Integer> call() throws Exception {
            try {
                String line;
                Map<String, Integer> result = new HashMap<>();
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split(" ");
                    for (String word : words) {
                        int count = result.getOrDefault(word, 0);
                        result.put(word, count + 1);
                    }
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
